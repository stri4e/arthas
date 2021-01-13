package com.github.arthas.models;

import com.github.arthas.HttpMethodType;
import com.github.arthas.annotations.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class DefaultMetaInfoBuilder implements IStaticMetaInfoBuilder {

    private Method method;

    private HttpMethod httpMethod;

    private String pathPattern;

    private Map<String, String> staticHeaders;

    private Class<?> responseType;

    private Class<?> bodyType;

    private HttpMethodType httpMethodType;

    private int bodyPosition = -1;

    private final Map<String, Integer> dynamicHeaders = new HashMap<>();

    private final Map<String, Integer> dynamicPaths = new HashMap<>();

    private final Map<String, Integer> dynamicQueries = new HashMap<>();

    @Override
    public IStaticMetaInfoBuilder method(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder httpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder annotation() {
        Class<? extends Annotation> clazz = chooseAnnotation(this.httpMethod);
        Annotation ann = AnnotationUtils.findAnnotation(this.method, clazz);
        Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(requireNonNull(ann));
        Header[] rawHeaders = (Header[]) attributes.get("headers");
        this.pathPattern = (String) attributes.get("path");
        this.staticHeaders = Arrays.stream(rawHeaders)
                .collect(Collectors.toMap(Header::name, Header::value));
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToFlux() {
        Annotation annResp;
        Annotation annBody;
        if ((annResp = AnnotationUtils.findAnnotation(this.method, ResponseToFlux.class)) != null) {
            this.responseType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annResp)).get("clazz");
            if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToMono.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.bodyMonoRespFlux;
            } else if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToFlux.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.onlyFlux;
            } else {
                httpMethodType = HttpMethodType.fluxWithoutBody;
                this.bodyType = Void.class;
            }
        }
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToMono() {
        Annotation annResp;
        Annotation annBody;
        if ((annResp = AnnotationUtils.findAnnotation(this.method, ResponseToMono.class)) != null) {
            this.responseType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annResp)).get("clazz");
            if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToMono.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.onlyMono;
            } else if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToFlux.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.bodyFluxRespMono;
            } else {
                this.bodyType = Void.class;
                this.httpMethodType = HttpMethodType.monoWithoutBody;
            }
        }
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToEmptyFlux() {
        Annotation annBody;
        if (AnnotationUtils.findAnnotation(this.method, ResponseToEmptyFlux.class) != null) {
            this.responseType = Void.class;
            if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToMono.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.bodyMonoRespFlux;
            } else if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToFlux.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.onlyFlux;
            } else {
                this.bodyType = Void.class;
                this.httpMethodType = HttpMethodType.fluxWithoutBody;
            }
        }
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToEmptyMono() {
        Annotation annBody;
        if (AnnotationUtils.findAnnotation(this.method, ResponseToEmptyMono.class) != null) {
            responseType = Void.class;
            if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToMono.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.onlyMono;
            } else if ((annBody = AnnotationUtils.findAnnotation(this.method, BodyToFlux.class)) != null) {
                this.bodyType = (Class<?>) AnnotationUtils.getAnnotationAttributes(requireNonNull(annBody)).get("clazz");
                this.httpMethodType = HttpMethodType.bodyFluxRespMono;
            } else {
                this.bodyType = Void.class;
                this.httpMethodType = HttpMethodType.monoWithoutBody;
            }
        }
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder methodParams() {
        Parameter[] params = this.method.getParameters();
        for (int i = 0; i < params.length; i++) {
            Body annBodyPar = AnnotationUtils.findAnnotation(params[i], Body.class);
            Path annPath = AnnotationUtils.findAnnotation(params[i], Path.class);
            Query annQuery = AnnotationUtils.findAnnotation(params[i], Query.class);
            Header annHeader = AnnotationUtils.findAnnotation(params[i], Header.class);
            if (Objects.nonNull(annBodyPar)) {
                this.bodyPosition = i;
            }
            if (Objects.nonNull(annPath)) {
                this.dynamicPaths.put(annPath.name(), i);
            }
            if (Objects.nonNull(annQuery)) {
                this.dynamicQueries.put(annQuery.name(), i);
            }
            if (Objects.nonNull(annHeader)) {
                this.dynamicHeaders.put(annHeader.name(), i);
            }
        }
        return this;
    }

    @Override
    public StaticMetaInfo build() {
        return new StaticMetaInfo(
                this.httpMethodType,
                this.httpMethod,
                Objects.isNull(this.staticHeaders) ? new HashMap<>() : this.staticHeaders,
                this.pathPattern,
                this.bodyType,
                this.responseType,
                this.dynamicHeaders,
                this.dynamicPaths,
                this.dynamicQueries,
                this.bodyPosition
        );
    }

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

}
