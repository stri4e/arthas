package com.github.arthas.http;

import com.github.arthas.HttpMethodType;
import com.github.arthas.annotations.*;
import com.github.arthas.models.StaticMetaInfo;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class AnnotationProcessor {

    public static Class<? extends Annotation> chooseAnnotation(HttpMethod method) {
        switch (method) {
            case GET:
                return Get.class;
            case PUT:
                return Put.class;
            case HEAD:
                return Head.class;
            case POST:
                return Post.class;
            case PATCH:
                return Patch.class;
            case TRACE:
                return Trace.class;
            case DELETE:
                return Delete.class;
            case OPTIONS:
                return Options.class;
            default:
                throw new RuntimeException("Annotation exist in default scope.");
        }
    }

    public static StaticMetaInfo staticMetaInfo(Method method, Class<? extends Annotation> clazz, HttpMethod httpMethod) {
        Annotation annResp;
        Annotation annBody;
        Class<?> responseType;
        Class<?> bodyType;
        HttpMethodType httpMethodType;
        Annotation ann = AnnotationUtils.findAnnotation(method, clazz);
        Map<String, Object> atrs = AnnotationUtils.getAnnotationAttributes(requireNonNull(ann));
        Header[] rawHeaders = (Header[]) atrs.get("headers");
        String path = (String) atrs.get("path");
        if ((annResp = AnnotationUtils.findAnnotation(method, ResponseToFlux.class)) != null) {
            responseType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annResp)).get("clazz");
            if ((annBody = AnnotationUtils.findAnnotation(method, BodyToMono.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.bodyMonoRespFlux;
            } else if ((annBody = AnnotationUtils.findAnnotation(method, BodyToFlux.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.onlyFlux;
            } else {
                httpMethodType = HttpMethodType.fluxWithoutBody;
                bodyType = Void.class;
            }
        } else if ((annResp = AnnotationUtils.findAnnotation(method, ResponseToMono.class)) != null) {
            responseType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annResp)).get("clazz");
            if ((annBody = AnnotationUtils.findAnnotation(method, BodyToMono.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.onlyMono;
            } else if ((annBody = AnnotationUtils.findAnnotation(method, BodyToFlux.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.bodyFluxRespMono;
            } else {
                bodyType = Void.class;
                httpMethodType = HttpMethodType.monoWithoutBody;
            }
        } else if (AnnotationUtils.findAnnotation(method, ResponseToEmptyFlux.class) != null) {
            responseType = Void.class;
            if ((annBody = AnnotationUtils.findAnnotation(method, BodyToMono.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.bodyMonoRespFlux;
            } else if ((annBody = AnnotationUtils.findAnnotation(method, BodyToFlux.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.onlyFlux;
            } else {
                bodyType = Void.class;
                httpMethodType = HttpMethodType.fluxWithoutBody;
            }
        } else if (AnnotationUtils.findAnnotation(method, ResponseToEmptyMono.class) != null) {
            responseType = Void.class;
            if ((annBody = AnnotationUtils.findAnnotation(method, BodyToMono.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.onlyMono;
            } else if ((annBody = AnnotationUtils.findAnnotation(method, BodyToFlux.class)) != null) {
                bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                httpMethodType = HttpMethodType.bodyFluxRespMono;
            } else {
                bodyType = Void.class;
                httpMethodType = HttpMethodType.monoWithoutBody;
            }
        } else {
            throw new RuntimeException();
        }
        return new StaticMetaInfo(httpMethodType, httpMethod, Arrays.stream(rawHeaders)
                .collect(Collectors.toMap(Header::name, Header::value)), path, bodyType, responseType);
    }

}
