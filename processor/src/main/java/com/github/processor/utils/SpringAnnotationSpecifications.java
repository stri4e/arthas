package com.github.processor.utils;

import com.squareup.javapoet.ClassName;

public class SpringAnnotationSpecifications {

    public static ClassName component() {
        return ClassName.get("org.springframework.stereotype", "Component");
    }

    public static ClassName getMapping() {
       return ClassName.get("org.springframework.web.bind.annotation", "GetMapping");
    }

    public static ClassName postMapping() {
        return ClassName.get("org.springframework.web.bind.annotation", "PostMapping");
    }

    public static ClassName deleteMapping() {
        return ClassName.get("org.springframework.web.bind.annotation", "DeleteMapping");
    }

    public static ClassName putMapping() {
        return ClassName.get("org.springframework.web.bind.annotation", "PutMapping");
    }

}
