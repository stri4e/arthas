package com.github.arthas.models;

import com.github.arthas.HttpMethodType;
import com.github.arthas.annotations.*;
import com.github.arthas.utils.ReflectParamsUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class ScanningGenericMetaInfoBuilder implements IStaticMetaInfoBuilder {

    private Method method;

    private HttpMethod httpMethod;

    private String pathPattern;

    private Map<String, String> staticHeaders;

    private Class<?> responseType;

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
    public IStaticMetaInfoBuilder responseTo() {
        Class<?> r = this.method.getReturnType();
        if (void.class.getName().equals(r.getName())) {
            this.responseType = r;
        } else {
            if (Flux.class.getName().equals(r.getName())) {
                this.responseType = (Class<?>) ReflectParamsUtils.fetchGenericType(this.method);
                this.httpMethodType = HttpMethodType.ofFlux;
            }else if (Mono.class.getName().equals(r.getName())) {
                this.responseType = (Class<?>) ReflectParamsUtils.fetchGenericType(this.method);
                this.httpMethodType = HttpMethodType.ofMono;
            } else {
                throw new RuntimeException("Can't not find Mono or Flux in return params. Method name: " + this.method.getName());
            }
        }
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToFlux() {
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToMono() {
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToEmptyFlux() {
        return this;
    }

    @Override
    public IStaticMetaInfoBuilder responseToEmptyMono() {
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
                this.method.getName(),
                this.httpMethodType,
                this.httpMethod,
                Objects.isNull(this.staticHeaders) ? new HashMap<>() : this.staticHeaders,
                this.pathPattern,
                null,
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
