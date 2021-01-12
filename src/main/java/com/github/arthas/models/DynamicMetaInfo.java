package com.github.arthas.models;

import com.github.arthas.annotations.*;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DynamicMetaInfo {

    private static final DynamicMetaInfo EMPTY = new DynamicMetaInfo();

    private final Map<String, Integer> headers = new HashMap<>();

    private final Map<String, Integer> paths = new HashMap<>();

    private final Map<String, Integer> queries = new HashMap<>();

    private Integer bodyPosition = -1;

    public Map<String, Integer> getHeaders() {
        return headers;
    }

    public Map<String, Integer> getPaths() {
        return paths;
    }

    public Map<String, Integer> getQueries() {
        return queries;
    }

    public Integer getBodyPosition() {
        return bodyPosition;
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public void collect(Parameter[] params) {
        for (int i = 0; i < params.length; i++) {
            Body annBody = AnnotationUtils.findAnnotation(params[i], Body.class);
            Path annPath = AnnotationUtils.findAnnotation(params[i], Path.class);
            Query annQuery = AnnotationUtils.findAnnotation(params[i], Query.class);
            Header annHeader = AnnotationUtils.findAnnotation(params[i], Header.class);
            if (Objects.nonNull(annBody)) {
                this.bodyPosition = i;
            }
            if (Objects.nonNull(annPath)) {
                this.paths.put(annPath.name(), i);
            }
            if (Objects.nonNull(annQuery)) {
                this.queries.put(annQuery.name(), i);
            }
            if (Objects.nonNull(annHeader)) {
                this.queries.put(annHeader.name(), i);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicMetaInfo that = (DynamicMetaInfo) o;
        return Objects.equals(headers, that.headers) &&
                Objects.equals(paths, that.paths) &&
                Objects.equals(queries, that.queries) &&
                Objects.equals(bodyPosition, that.bodyPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers, paths, queries, bodyPosition);
    }

    @Override
    public String toString() {
        return "DynamicMetaInfo{" +
                "headers=" + headers +
                ", paths=" + paths +
                ", queries=" + queries +
                ", bodyPosition=" + bodyPosition +
                '}';
    }
}
