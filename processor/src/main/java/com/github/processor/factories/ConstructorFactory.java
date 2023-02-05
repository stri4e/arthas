package com.github.processor.factories;

import com.github.processor.annotations.Arthas;
import com.github.processor.utils.SpringTypeSpecifications;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;

public class ConstructorFactory {

    public static MethodSpec constructor(Arthas annotation) {
        String url = StringUtils.isNoneBlank(annotation.baseUrl()) ? annotation.baseUrl() : annotation.instanceName();
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(SpringTypeSpecifications.webClientBuilder(), "clientBuilder")
                .addStatement(String.format("this.client = clientBuilder.baseUrl(\"%s\").build()", url))
                .build();
    }

}
