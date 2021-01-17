package com.github.arthas.utils;

import com.github.arthas.annotations.*;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Objects;

public class ArthasAnnotationUtils {

    public static boolean isOnePresent(Method m) {
        BodyToFlux bodyToFlux = AnnotationUtils.findAnnotation(m, BodyToFlux.class);
        BodyToMono bodyToMono = AnnotationUtils.findAnnotation(m, BodyToMono.class);
        ResponseToEmptyFlux responseToEmptyFlux = AnnotationUtils.findAnnotation(m, ResponseToEmptyFlux.class);
        ResponseToEmptyMono responseToEmptyMono = AnnotationUtils.findAnnotation(m, ResponseToEmptyMono.class);
        ResponseToFlux responseToFlux = AnnotationUtils.findAnnotation(m, ResponseToFlux.class);
        ResponseToMono responseToMono = AnnotationUtils.findAnnotation(m, ResponseToMono.class);
        return Objects.nonNull(bodyToFlux) ||
                Objects.nonNull(bodyToMono) ||
                Objects.nonNull(responseToEmptyFlux) ||
                Objects.nonNull(responseToEmptyMono) ||
                Objects.nonNull(responseToFlux) ||
                Objects.nonNull(responseToMono);
    }

}
