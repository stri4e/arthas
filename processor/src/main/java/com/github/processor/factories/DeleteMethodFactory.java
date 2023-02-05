package com.github.processor.factories;

import com.github.processor.annotations.AnnotationRESTMethodsEmulator;
import com.github.processor.annotations.Arthas;
import com.github.processor.code.RequestHeadersCodeBlock;
import com.github.processor.code.URICodeBlock;
import com.github.processor.utils.ReturnCodeBlocks;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteMethodFactory implements RESTMethodsFactory {

    @Override
    public MethodSpec method(Element method, Arthas arthas) {
        AnnotationRESTMethodsEmulator annotation = new AnnotationRESTMethodsEmulator(method, AnnotationRESTMethodsEmulator.DELETE_MAPPING);
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
