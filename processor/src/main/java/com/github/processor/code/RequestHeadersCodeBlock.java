package com.github.processor.code;

import com.github.processor.annotations.ParamEmulator;
import com.github.processor.utils.SpringTypeSpecifications;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.VariableElement;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RequestHeadersCodeBlock {

    public static CodeBlock requestAttribute(List<? extends VariableElement> parameters) {
        return parameters.stream().map(param -> {
            String parameterName = param.getSimpleName().toString();
            String annotationParameterName;
            ParamEmulator requestAttribute = new ParamEmulator(param, ParamEmulator.REQUEST_ATTRIBUTE);
            if (requestAttribute.isAnnotationExist()) {
                String name = requestAttribute.name();
                annotationParameterName = StringUtils.isNoneBlank(name) ? name : requestAttribute.value();
                if (StringUtils.isBlank(annotationParameterName)) {
                    annotationParameterName = parameterName;
                }
                return CodeBlock.of(".attribute($S, $L)", annotationParameterName, parameterName);
            }
            return null;
        }).filter(Objects::nonNull).collect(CodeBlock.joining(""));
    }

    public static CodeBlock requestHeaders(List<? extends VariableElement> parameters) {
        return parameters.stream().map(param -> {
            String parameterName = param.getSimpleName().toString();
            String annotationParameterName;
            ParamEmulator requestHeader = new ParamEmulator(param, ParamEmulator.REQUEST_HEADER);
            if (requestHeader.isAnnotationExist()) {
                String name = requestHeader.name();
                String defValue = requestHeader.defaultValue();
                annotationParameterName = StringUtils.isNoneBlank(name) ? name : requestHeader.value();
                if (StringUtils.isBlank(annotationParameterName)) {
                    annotationParameterName = parameterName;
                }
                return CodeBlock.of(".header($S, $T.isNull($L) ? $S : $L)",
                        annotationParameterName,
                        ParameterizedTypeName.get(Objects.class),
                        parameterName,
                        defValue,
                        parameterName
                );
            }
            return null;
        }).filter(Objects::nonNull).collect(CodeBlock.joining(""));
    }

    public static CodeBlock requestAccept(List<String> consumers) {
        CodeBlock code = consumers.stream()
                .map(type -> CodeBlock.of("$T.valueOf($S)", SpringTypeSpecifications.mediaType(), type))
                .collect(CodeBlock.joining(",$W"));
        List<CodeBlock> result = new LinkedList<>();
        result.add(CodeBlock.of(".accept("));
        result.add(code);
        result.add(CodeBlock.of(")"));
        return CodeBlock.join(result, "");
    }

    public static CodeBlock requestCookies(List<? extends VariableElement> parameters) {
        return parameters.stream().map(param -> {
            String parameterName = param.getSimpleName().toString();
            String annotationParameterName;
            ParamEmulator requestCookie = new ParamEmulator(param, ParamEmulator.COOKIE_VALUE);
            if (requestCookie.isAnnotationExist()) {
                String name = requestCookie.name();
                String defValue = requestCookie.defaultValue();
                annotationParameterName = StringUtils.isNoneBlank(name) ? name : requestCookie.value();
                if (!StringUtils.isNoneBlank(annotationParameterName)) {
                    annotationParameterName = parameterName;
                }
                return CodeBlock.of(".cookie($S, $T.isNull($L) ? $S : $L)",
                        annotationParameterName,
                        ParameterizedTypeName.get(Objects.class),
                        parameterName,
                        defValue,
                        parameterName
                );
            }
            return null;
        }).filter(Objects::nonNull).collect(CodeBlock.joining(""));
    }

}
