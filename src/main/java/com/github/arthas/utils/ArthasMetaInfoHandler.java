package com.github.arthas.utils;

import com.github.arthas.annotations.*;
import com.github.arthas.models.IStaticMetaInfoBuilder;
import com.github.arthas.models.StaticMetaInfo;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

public class ArthasMetaInfoHandler {

    private static final Predicate<Method> isAnnotationsPresent = method ->
            Objects.nonNull(AnnotationUtils.findAnnotation(method, BodyToFlux.class)) ||
            Objects.nonNull(AnnotationUtils.findAnnotation(method, BodyToMono.class)) ||
            Objects.nonNull(AnnotationUtils.findAnnotation(method, ResponseToEmptyFlux.class)) ||
            Objects.nonNull(AnnotationUtils.findAnnotation(method, ResponseToEmptyMono.class)) ||
            Objects.nonNull(AnnotationUtils.findAnnotation(method, ResponseToFlux.class)) ||
            Objects.nonNull(AnnotationUtils.findAnnotation(method, ResponseToMono.class));

    private static final BiFunction<Method, HttpMethod, StaticMetaInfo> withAnnotations = (method, httpMethod) ->
            IStaticMetaInfoBuilder.builder()
                    .method(method)
                    .httpMethod(httpMethod)
                    .annotation()
                    .responseToFlux()
                    .responseToMono()
                    .responseToEmptyFlux()
                    .responseToEmptyMono()
                    .methodParams()
                    .build();


    private static final BiFunction<Method, HttpMethod, StaticMetaInfo> withoutAnnotations = (method, httpMethod) ->
            IStaticMetaInfoBuilder.scanningBuilder()
                    .method(method)
                    .httpMethod(httpMethod)
                    .annotation()
                    .responseTo()
                    .methodParams()
                    .build();

    private static final Function<Method, Optional<StaticMetaInfo>> findMetaInfo = (method) -> {
        BaseMethod bm = AnnotationUtils.findAnnotation(method, BaseMethod.class);
        if (Objects.nonNull(bm)) {
            if (isAnnotationsPresent.test(method)) {
                return Optional.ofNullable(withAnnotations.apply(method, bm.method()));
            } else {
                return Optional.ofNullable(withoutAnnotations.apply(method, bm.method()));
            }
        }
        return Optional.empty();
    };

    public static Optional<StaticMetaInfo> generateMetaInfo(Method method) {
        return findMetaInfo.apply(method);
    }

}
