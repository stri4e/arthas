package com.github.processor.annotations;

import com.github.processor.utils.ReflectionUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParamEmulator {

    public static final String DEFAULT_VALUE = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

    public static final String PATH_VARIABLE = "PathVariable";
    public static final String REQUEST_ATTRIBUTE = "RequestAttribute";

    public static final String REQUEST_PARAM = "RequestParam";
    public static final String REQUEST_HEADER = "RequestHeader";
    public static final String COOKIE_VALUE = "CookieValue";

    private Map<? extends ExecutableElement, ? extends AnnotationValue> elementsValues;

    private final String annotationName;

    private AnnotationMirror annotation;

    private VariableElement variableElement;

    private ExecutableElement value;
    private ExecutableElement name;
    private ExecutableElement required;
    private ExecutableElement defaultValue;

    public ParamEmulator(VariableElement variableElement, String name) {
        this.annotationName = name;
        List<? extends AnnotationMirror> annotationMirrors = variableElement.getAnnotationMirrors();
        if (Objects.nonNull(annotationMirrors) && !annotationMirrors.isEmpty()) {
            this.annotation = annotationMirrors.stream()
                    .filter(mirror -> mirror.getAnnotationType().asElement()
                            .getSimpleName().toString().equals(name)
                    ).findFirst().orElse(null);
            this.variableElement = variableElement;
            if (Objects.nonNull(this.annotation)) {
                this.elementsValues = this.annotation.getElementValues();
                this.value = this.annotation.getElementValues().keySet().stream()
                        .filter(e -> e.toString().equals("value()"))
                        .findFirst().orElse(null);
                this.name = this.annotation.getElementValues().keySet().stream()
                        .filter(e -> e.toString().equals("name()"))
                        .findFirst().orElse(null);
                this.required = this.annotation.getElementValues().keySet().stream()
                        .filter(e -> e.toString().equals("required()"))
                        .findFirst().orElse(null);
                this.defaultValue = this.annotation.getElementValues().keySet().stream()
                        .filter(e -> e.toString().equals("defaultValue()"))
                        .findFirst().orElse(null);
            }
        }
    }

    public String value() {
        AnnotationValue annotationValue = this.elementsValues.get(this.value);
        if (Objects.nonNull(annotationValue)) {
            Object result = annotationValue.getValue();
            if (result instanceof String) {
                return (String) result;
            }
            return ReflectionUtils.getValFromAttributeConstant(result, String.class);
        }
        return "";
    }

    public String name() {
        AnnotationValue annotationValue = this.elementsValues.get(this.name);
        if (Objects.nonNull(annotationValue)) {
            Object result = annotationValue.getValue();
            if (result instanceof String) {
                return (String) result;
            }
            return ReflectionUtils.getValFromAttributeConstant(result, String.class);
        }
        return "";
    }

    public Boolean required() {
        AnnotationValue annotationValue = this.elementsValues.get(this.required);
        if (Objects.nonNull(annotationValue)) {
            Object result = annotationValue.getValue();
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return ReflectionUtils.getValFromAttributeConstant(result, Boolean.class);
        }
        return Boolean.TRUE;
    }

    public String defaultValue() {
        if (this.annotationName.equals(REQUEST_PARAM) ||
                this.annotationName.equals(REQUEST_HEADER) ||
                this.annotationName.equals(COOKIE_VALUE)) {
            AnnotationValue annotationValue = this.elementsValues.get(this.defaultValue);
            if (Objects.nonNull(annotationValue)) {
                Object result = annotationValue.getValue();
                if (result instanceof String) {
                    return (String) result;
                }
                return ReflectionUtils.getValFromAttributeConstant(result, String.class);
            } else {
                return DEFAULT_VALUE;
            }
        }
        return null;
    }

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public boolean isAnnotationExist() {
        return Objects.nonNull(this.annotation);
    }


}
