package com.github.processor.factories;

import com.github.processor.annotations.AnnotationRESTMethodsEmulator;
import com.github.processor.annotations.Arthas;
import com.github.processor.annotations.ParamEmulator;
import com.github.processor.code.RequestHeadersCodeBlock;
import com.github.processor.code.URICodeBlock;
import com.github.processor.utils.AnnotationPair;
import com.github.processor.utils.ReturnCodeBlocks;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

public class GetMethodFactory implements RESTMethodsFactory {

    @Override
    public MethodSpec method(Element method, Arthas arthas) {
        AnnotationRESTMethodsEmulator annotation = new AnnotationRESTMethodsEmulator(method, AnnotationRESTMethodsEmulator.GET_MAPPING);
        if (annotation.isAnnotationExist()) {
            List<String> consumes = annotation.consumes();
            List<String> paths = annotation.path();
            if (paths.isEmpty()) {
                paths = annotation.values();
            }
            ExecutableElement methodExecutableElement = (ExecutableElement) method;
            TypeMirror methodReturnType = methodExecutableElement.getReturnType();
            List<? extends VariableElement> parameters = methodExecutableElement.getParameters();
            if (methodReturnType instanceof DeclaredType) {

                List<AnnotationPair> y = parameters.stream()
                        .map(param -> {
                            String parameterName = param.getSimpleName().toString();
                            ParamEmulator requestParam = new ParamEmulator(param, ParamEmulator.PATH_VARIABLE);
                            if (requestParam.isAnnotationExist()) {
                                String name = requestParam.name();
                                String annotationParameterName = StringUtils.isNoneBlank(name) ? name : requestParam.value();
                                if (StringUtils.isBlank(annotationParameterName)) {
                                    annotationParameterName = parameterName;
                                }
                                return new AnnotationPair(annotationParameterName, parameterName);
                            }
                            return null;
                        }).filter(Objects::nonNull).collect(Collectors.toList());

                Map<String, List<String>> mappers = new HashMap<>();
                for (int i = 0; i < paths.size(); i++) {
                    String path = paths.get(i);
                    List<String> args = new LinkedList<>();
                    for (int j = 0; j < y.size(); j++) {
                        AnnotationPair tmp = y.get(j);
                        if (path.contains(tmp.getKey())) {
                            args.add(tmp.getValue());
                        }
                    }
                    mappers.put(path, args);
                }

                CodeBlock.Builder c = CodeBlock.builder()
                        .addStatement("$T path = \"\"", ClassName.get(String.class));
                int count = 0;
                for (String key : mappers.keySet()) {
                    List<String> values = mappers.get(key);
                    List<CodeBlock> cbs = new LinkedList<>();
                    for (String value : values) {
                        cbs.add(CodeBlock.builder()
                                .add("$T.nonNull($L)", ClassName.get(Objects.class), value)
                                .build());
                    }
                    if (count++ == 0) {
                        c.beginControlFlow("if ($L)", CodeBlock.join(cbs, " && "))
                                .addStatement("path = $S", key);
                    } else {
                        c.nextControlFlow("else if ($L)", CodeBlock.join(cbs, " && "))
                                .addStatement("path = $S", key);
                    }
                }
                CodeBlock controlFlow = c.endControlFlow().build();

                ParameterizedTypeName typeHashMap = ParameterizedTypeName.get(HashMap.class, String.class, Object.class);
                ParameterizedTypeName variableMap = ParameterizedTypeName.get(Map.class, String.class, Object.class);

                DeclaredType declaredMethodReturnType = (DeclaredType) methodReturnType;
                List<ParameterSpec> convertedParameters = parameters.stream()
                        .map(param -> ParameterSpec.builder(
                                ParameterizedTypeName.get(param.asType()),
                                param.getSimpleName().toString()).build()
                        ).collect(Collectors.toList());

                CodeBlock parameterOfHttpPath = URICodeBlock.pathParameters(parameters);
                CodeBlock httpAttributes = RequestHeadersCodeBlock.requestAttribute(parameters);
                CodeBlock httpHeaders = RequestHeadersCodeBlock.requestHeaders(parameters);
                CodeBlock httpCookies = RequestHeadersCodeBlock.requestCookies(parameters);
                CodeBlock uriComponentsCode = URICodeBlock.uriCodeBlock(paths, parameters, parameterOfHttpPath);

                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString());
                methodSpecBuilder.addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameters(convertedParameters)
                        .returns(ParameterizedTypeName.get(declaredMethodReturnType));

                if (paths.size() > 1) {
                    methodSpecBuilder.addCode(controlFlow);
                }

                if (!parameterOfHttpPath.isEmpty()) {
                    methodSpecBuilder.addStatement(CodeBlock.of("$T parameters = new $T()", variableMap, typeHashMap));
                    methodSpecBuilder.addCode(parameterOfHttpPath);
                }

                methodSpecBuilder.addStatement(uriComponentsCode);

                methodSpecBuilder.addStatement(ReturnCodeBlocks.builder()
                        .returnCodeBlock()
                        .uriCodeBlock()
                        .httpAttributesCodeBlock(httpAttributes)
                        .httpHeadersCodeBlock(httpHeaders)
                        .httpCookiesCodeBlock(httpCookies)
                        .requestAcceptCodeBlock(consumes)
                        .retrieveCodeBlock()
                        .bodyToPublisherCodeBlock(methodReturnType)
                        .build());

                return methodSpecBuilder.build();
            }
        }
        return null;
    }

}
