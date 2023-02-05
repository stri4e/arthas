package com.github.processor.mappers;

import com.github.processor.utils.SpringTypeSpecifications;
import com.squareup.javapoet.*;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import java.util.Objects;

// TODO: 25.12.22 check on wildcard
public class BodyToPublisher {

    public static CodeBlock bodyToPublisher(TypeMirror methodReturnType) {
        PublisherChooser publisher = PublisherChooser.containsPublisher(methodReturnType);
        DeclaredType declaredMethodReturnType = (DeclaredType) methodReturnType;
        if (declaredMethodReturnType.getTypeArguments().size() != 0) {
            TypeMirror generic = declaredMethodReturnType.getTypeArguments().get(0);
            if (generic instanceof WildcardType) {
                return Objects.requireNonNull(publisher).bodyToWildcard(generic);
            }
            return Objects.requireNonNull(publisher).bodyToPublisher(generic);
        } else {
            return Objects.requireNonNull(publisher).bodyToUndefined();
        }
    }

    public enum PublisherChooser {
        mono(SpringTypeSpecifications.mono().toString()) {
            @Override
            public CodeBlock bodyToPublisher(TypeMirror generic) {
                CodeBlock result;
                DeclaredType typeOfSecondGeneric = (DeclaredType) generic;
                if (typeOfSecondGeneric.getTypeArguments().size() != 0) {
                    String secondGeneric = typeOfSecondGeneric.toString();
                    if (secondGeneric.contains(SpringTypeSpecifications.responseEntity().toString())) {
                        DeclaredType innerGeneric = (DeclaredType) typeOfSecondGeneric.getTypeArguments().get(0);
                        if (innerGeneric.getTypeArguments().size() != 0) {
                            if (innerGeneric.toString().contains(SpringTypeSpecifications.flux().toString())) {
                                DeclaredType secondInnerGeneric = (DeclaredType) innerGeneric.getTypeArguments().get(0);
                                if (secondInnerGeneric.getTypeArguments().size() != 0) {
                                    result = CodeBlock.of(".toEntityFlux($L)", TypeSpec.anonymousClassBuilder("")
                                            .addSuperinterface(ParameterizedTypeName.get(
                                                    SpringTypeSpecifications.parameterizedTypeReference(), TypeName.get(secondInnerGeneric))
                                            ).build());
                                } else {
                                    result = CodeBlock.of(".toEntityFlux($T.$L)", ParameterizedTypeName.get(secondInnerGeneric), "class");
                                }
                            } else {
                                result = CodeBlock.of(".toEntity($L)", TypeSpec.anonymousClassBuilder("")
                                        .addSuperinterface(ParameterizedTypeName.get(
                                                SpringTypeSpecifications.parameterizedTypeReference(), TypeName.get(innerGeneric))
                                        ).build());
                            }
                        } else {
                            result = CodeBlock.of(".toEntity($T.$L)", ParameterizedTypeName.get(innerGeneric), "class");
                        }
                    } else {
                        result = CodeBlock.of(".bodyToMono($L)", TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(ParameterizedTypeName.get(
                                        SpringTypeSpecifications.parameterizedTypeReference(), TypeName.get(generic))
                                ).build());
                    }
                } else {
                    result = CodeBlock.of(".bodyToMono($T.$L)", ParameterizedTypeName.get(generic), "class");
                }
                return result;
            }

            @Override
            public CodeBlock bodyToUndefined() {
                return CodeBlock.of(".bodyToMono($T.$L)", ParameterizedTypeName.get(Object.class), "class");
            }

            @Override
            public CodeBlock bodyToWildcard(TypeMirror generic) {
                return CodeBlock.of(".bodyToMono($T.$L)", ParameterizedTypeName.get(Object.class), "class");
            }
        },

        flux(SpringTypeSpecifications.flux().toString()) {
            @Override
            public CodeBlock bodyToPublisher(TypeMirror generic) {
                CodeBlock result;
                DeclaredType typeOfSecondGeneric = (DeclaredType) generic;
                if (typeOfSecondGeneric.getTypeArguments().size() != 0) {
                    result = CodeBlock.of(".bodyToFlux($L)", TypeSpec.anonymousClassBuilder("")
                            .addSuperinterface(ParameterizedTypeName.get(
                                    SpringTypeSpecifications.parameterizedTypeReference(), TypeName.get(generic))
                            ).build());
                } else {
                    result = CodeBlock.of(".bodyToFlux($T.$L)", ParameterizedTypeName.get(generic), "class");
                }
                return result;
            }

            @Override
            public CodeBlock bodyToUndefined() {
                return CodeBlock.of(".bodyToFlux($T.$L)", ParameterizedTypeName.get(Object.class), "class");
            }

            @Override
            public CodeBlock bodyToWildcard(TypeMirror generic) {
                return CodeBlock.of(".bodyToFlux($T.$L)", ParameterizedTypeName.get(Object.class), "class");
            }
        },

        def(ClassName.OBJECT.reflectionName()) {
            @Override
            public CodeBlock bodyToPublisher(TypeMirror generic) {
                return null;
            }

            @Override
            public CodeBlock bodyToUndefined() {
                return null;
            }

            @Override
            public CodeBlock bodyToWildcard(TypeMirror generic) {
                return null;
            }
        };

        final String name;

        PublisherChooser(String name) {
            this.name = name;
        }

        public abstract CodeBlock bodyToPublisher(TypeMirror generic);

        public abstract CodeBlock bodyToUndefined();

        public abstract CodeBlock bodyToWildcard(TypeMirror generic);

        public static PublisherChooser containsPublisher(TypeMirror returnType) {
            String type = returnType.toString();
            if (type.contains(mono.name)) {
                return mono;
            } else if (type.contains(flux.name)) {
                return flux;
            } else {
                throw  new IllegalArgumentException("Method should return Mono or Flux");
            }
        }

        public static PublisherChooser publisherOrDef(TypeMirror returnType) {
            String type = returnType.toString();
            if (type.contains(mono.name)) {
                return mono;
            } else if (type.contains(flux.name)) {
                return flux;
            } else {
                return def;
            }
        }

        public boolean isMono() {
            return this.name.equals(SpringTypeSpecifications.mono().toString());
        }

        public boolean isFlux() {
            return this.name.equals(SpringTypeSpecifications.flux().toString());
        }

    }

}
