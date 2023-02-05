package com.github.processor.annotations;

import com.github.processor.utils.ReflectionUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestBodyEmulator {

    private Map<? extends ExecutableElement, ? extends AnnotationValue> elementsValues;

    private AnnotationMirror annotation;

    private VariableElement variableElement;

    private ExecutableElement required;

    public RequestBodyEmulator(VariableElement variableElement) {
        List<? extends AnnotationMirror> annotationMirrors = variableElement.getAnnotationMirrors();
        if (Objects.nonNull(annotationMirrors) && !annotationMirrors.isEmpty()) {
            this.annotation = annotationMirrors.stream()
                    .filter(mirror -> mirror.getAnnotationType().asElement()
                            .getSimpleName().toString().equals("RequestBody")
                    ).findFirst().orElse(null);
            this.variableElement = variableElement;
            if (Objects.nonNull(this.annotation)) {
                this.elementsValues = this.annotation.getElementValues();
                this.required = this.annotation.getElementValues().keySet().stream()
                        .filter(e -> e.toString().equals("required()"))
                        .findFirst().orElse(null);
            }
        }
    }

    public Boolean required() {
        AnnotationValue annotationValue = this.elementsValues.get(this.required);
        if (Objects.nonNull(annotationValue)) {
            return ReflectionUtils.getValFromAttributeConstant(annotationValue.getValue(), Boolean.class);
        }
        return Boolean.TRUE;
    }

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public boolean isAnnotationExist() {
        return Objects.nonNull(this.annotation);
    }

}
