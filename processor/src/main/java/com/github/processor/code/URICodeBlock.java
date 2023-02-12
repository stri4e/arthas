package com.github.processor.code;

import com.github.processor.annotations.ParamEmulator;
import com.github.processor.utils.SpringTypeSpecifications;
import com.github.processor.utils.TypesUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class URICodeBlock {

    public static CodeBlock uriBuilder() {
        return CodeBlock.of("$T uri = $T.newInstance().uri(this.baseUri)", URI.class, SpringTypeSpecifications.uriComponentsBuilder());
    }

    public static CodeBlock uri() {
        return CodeBlock.of(".uri(uri)");
    }

    public static CodeBlock path(String path) {
        return CodeBlock.of(".path($S)", path);
    }

    public static CodeBlock pathName(String path) {
        return CodeBlock.of(".path($N)", path);
    }

    public static CodeBlock query(List<? extends VariableElement> parameters) {
        return queryParameters(parameters);
    }

    public static CodeBlock uriBuilder(CodeBlock parameterOfHttpPath) {
        if (!parameterOfHttpPath.isEmpty()) {
            return CodeBlock.of(".build(parameters)");
        } else {
            return CodeBlock.of(".build().toUri()");
        }
    }

    public static CodeBlock pathParameters(List<? extends VariableElement> parameters) {
        return parameters.stream().map(param -> {
            String parameterName = param.getSimpleName().toString();
            String annotationParameterName;
            ParamEmulator requestParam = new ParamEmulator(param, ParamEmulator.PATH_VARIABLE);
            if (requestParam.isAnnotationExist()) {
                String name = requestParam.name();
                annotationParameterName = StringUtils.isNoneBlank(name) ? name : requestParam.value();
                if (StringUtils.isBlank(annotationParameterName)) {
                    annotationParameterName = parameterName;
                }
                return requestParam.required() ? CodeBlock.of("parameters.put($S, $T.requireNonNull($N));\n",
                        annotationParameterName, ParameterizedTypeName.get(Objects.class), parameterName)
                        : CodeBlock.of("parameters.put($S, $N);\n", annotationParameterName, parameterName);
            }
            return null;
        }).filter(Objects::nonNull).collect(CodeBlock.joining(""));
    }

    public static CodeBlock queryParameters(List<? extends VariableElement> parameters) {
        return parameters.stream().map(param -> {
            String parameterName = param.getSimpleName().toString();
            String annotationParameterName;
            ParamEmulator requestParam = new ParamEmulator(param, ParamEmulator.REQUEST_PARAM);
            if (requestParam.isAnnotationExist()) {
                String name = requestParam.name();
                annotationParameterName = StringUtils.isNoneBlank(name) ? name : requestParam.value();
                if (StringUtils.isBlank(annotationParameterName)) {
                    annotationParameterName = parameterName;
                }
                TypeMirror type = param.asType();
                if (TypesUtils.isMultiValueMap(type)) {
                    return CodeBlock.of(".queryParams($S, $L)", annotationParameterName, parameterName);
                } else if (TypesUtils.isOptional(type)) {
                    return CodeBlock.of(".queryParamIfPresent($S, $L)", annotationParameterName, parameterName);
                } else if (!requestParam.required()) {
                    ClassName className = ClassName.get(Optional.class);
                    return CodeBlock.of(".queryParamIfPresent($S, $T.ofNullable($L))", annotationParameterName, className, parameterName);
                } else {
                    return CodeBlock.of(".queryParam($S, $L)", annotationParameterName, parameterName);
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(CodeBlock.joining(""));
    }

    public static CodeBlock uriCodeBlock(List<String> paths, List<? extends VariableElement> parameters, CodeBlock parameterOfHttpPath) {
        List<CodeBlock> uriComponents = new ArrayList<>();
        CodeBlock builder = URICodeBlock.uriBuilder();

        uriComponents.add(builder);
        if (paths.size() == 1) {
            uriComponents.add(URICodeBlock.path(paths.get(0)));
        } else if (paths.size() > 1) {
            uriComponents.add(URICodeBlock.pathName("path"));
        } else {
            uriComponents.add(URICodeBlock.path("/"));
        }

        uriComponents.add(URICodeBlock.query(parameters));
        uriComponents.add(URICodeBlock.uriBuilder(parameterOfHttpPath));

        return CodeBlock.join(uriComponents, "$Z");
    }

}
