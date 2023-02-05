package com.github.processor.utils;

import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;

import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.stream.Collectors;

public class ParametersUtils {

    public static List<ParameterSpec> convertedParameters(List<? extends VariableElement> parameters) {
        return parameters.stream()
                .map(param -> ParameterSpec.builder(
                        ParameterizedTypeName.get(param.asType()),
                        param.getSimpleName().toString()).build()
                ).collect(Collectors.toList());
    }

}
