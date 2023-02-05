package com.github.processor.code;

import com.github.processor.annotations.RequestBodyEmulator;
import com.github.processor.mappers.BodyToPublisher;
import com.github.processor.utils.SpringTypeSpecifications;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

public class RequestBodyCodeBlock {

    public static CodeBlock body(List<? extends VariableElement> parameters) {
        return parameters.stream()
                .map(RequestBodyEmulator::new)
                .filter(RequestBodyEmulator::isAnnotationExist)
                .map(param -> {
                    VariableElement elem = param.getVariableElement();
                    TypeMirror type = elem.asType();
                    CodeBlock codeBlock = CodeBlock.builder().build();
                    BodyToPublisher.PublisherChooser publisher = BodyToPublisher.PublisherChooser
                            .publisherOrDef(type);
                    String parameterName = elem.getSimpleName().toString();
                    if (publisher.isMono() || publisher.isFlux()) {
                        DeclaredType generic = (DeclaredType) type;
                        if (generic.getTypeArguments().size() != 0) {
                            DeclaredType secondGeneric = (DeclaredType) generic.getTypeArguments().get(0);
                            if (secondGeneric.getTypeArguments().size() != 0) {
                                codeBlock = CodeBlock.of(".body($N, $L)", parameterName,
                                        TypeSpec.anonymousClassBuilder("")
                                                .addSuperinterface(
                                                        ParameterizedTypeName.get(
                                                                SpringTypeSpecifications.parameterizedTypeReference(),
                                                                TypeName.get(secondGeneric))
                                                ).build()
                                );
                            } else {
                                codeBlock = CodeBlock.of(".body($N, $T.$L)", parameterName,
                                        ParameterizedTypeName.get(secondGeneric), "class");
                            }
                        }
                    } else {
                        codeBlock = CodeBlock.of(".bodyValue($N)", parameterName);
                    }
                    return codeBlock;
                }).findFirst().orElse(CodeBlock.builder().build());
    }

}
