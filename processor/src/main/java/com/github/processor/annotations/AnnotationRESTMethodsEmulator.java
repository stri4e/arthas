package com.github.processor.annotations;

import com.github.processor.utils.ReflectionUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AnnotationRESTMethodsEmulator {

    public static final String DELETE_MAPPING = "DeleteMapping";
    public static final String GET_MAPPING = "GetMapping";
    public static final String POST_MAPPING = "PostMapping";
    public static final String PUT_MAPPING = "PutMapping";

    private Map<? extends ExecutableElement, ? extends AnnotationValue> elementsValues;

    private AnnotationMirror annotation;

    private ExecutableElement name;
    private ExecutableElement values;
    private ExecutableElement path;
    private ExecutableElement params;
    private ExecutableElement headers;
    private ExecutableElement consumes;
    private ExecutableElement produce;

    public AnnotationRESTMethodsEmulator(Element method, String annotationSimpleName) {
        List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
        if (Objects.nonNull(annotationMirrors) && !annotationMirrors.isEmpty()) {
            this.annotation = annotationMirrors.stream()
                    .filter(mirror -> mirror.getAnnotationType().asElement()
                            .getSimpleName().toString().equals(annotationSimpleName)
                    ).findFirst().orElse(null);
            if (Objects.nonNull(this.annotation)) {
                this.elementsValues = this.annotation.getElementValues();
                this.name = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("name()"))
                        .findFirst().orElse(null);
                this.path = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("path()"))
                        .findFirst().orElse(null);
                this.values = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("value()"))
                        .findFirst().orElse(null);
                this.params = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("params()"))
                        .findFirst().orElse(null);
                this.headers = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("headers()"))
                        .findFirst().orElse(null);
                this.consumes = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("consumes()"))
                        .findFirst().orElse(null);
                this.produce = this.elementsValues.keySet().stream()
                        .filter(e -> e.toString().equals("produces()"))
                        .findFirst().orElse(null);
            }
        }
    }

    public boolean isAnnotationExist() {
        return Objects.nonNull(this.annotation);
    }

    public String name() {
        AnnotationValue annotationValue = this.elementsValues.get(this.name);
        if (Objects.nonNull(annotationValue)) {
            return ReflectionUtils.getValFromAttributeConstant(annotationValue.getValue(), String.class);
        }
        return "";
    }

    public List<String> values() {
        AnnotationValue annotationValue = this.elementsValues.get(this.values);
        if (Objects.nonNull(annotationValue)) {
            return getValues(annotationValue);
        }
        return new ArrayList<>();
    }

    public List<String> path() {
        AnnotationValue annotationValue = this.elementsValues.get(this.path);
        if (Objects.nonNull(annotationValue)) {
            return getValues(annotationValue);
        }
        return new ArrayList<>();
    }

    public List<String> params() {
        AnnotationValue annotationValue = this.elementsValues.get(this.params);
        if (Objects.nonNull(annotationValue)) {
            return getValues(annotationValue);
        }
        return new ArrayList<>();
    }

    public List<String> headers() {
        AnnotationValue annotationValue = this.elementsValues.get(this.headers);
        if (Objects.nonNull(annotationValue)) {
            return getValues(annotationValue);
        }
        return new ArrayList<>();
    }

    public List<String> consumes() {
        AnnotationValue annotationValue = this.elementsValues.get(this.consumes);
        if (Objects.nonNull(annotationValue)) {
            return getValues(annotationValue);
        }
        return new ArrayList<>();
    }

    public List<String> produce() {
        AnnotationValue annotationValue = this.elementsValues.get(this.produce);
        if (Objects.nonNull(annotationValue)) {
            return getValues(annotationValue);
        }
        return new ArrayList<>();
    }

    private List<String> getValues(AnnotationValue annotationValue) {
        @SuppressWarnings("unchecked") List<Object> values = (List<Object>) annotationValue.getValue();
        return values.stream()
                .map(val -> ReflectionUtils.getValFromAttributeConstant(val, String.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
