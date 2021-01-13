package com.github.arthas.models;

import com.github.arthas.HttpMethodType;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.Objects;

public final class StaticMetaInfo {

    private final HttpMethodType httpMethodType;

    private final HttpMethod httpMethod;

    private final Map<String, String> staticHeaders;

    private final String pathPattern;

    private final Class<?> bodyType;

    private final Class<?> responseType;

    private final Map<String, Integer> headers;

    private final Map<String, Integer> paths;

    private final Map<String, Integer> queries;

    private Integer bodyPosition = -1;

    public StaticMetaInfo(HttpMethodType httpMethodType,
                          HttpMethod httpMethod,
                          Map<String, String> staticHeaders,
                          String pathPattern,
                          Class<?> bodyType, Class<?> responseType,
                          Map<String, Integer> headers, Map<String, Integer> paths,
                          Map<String, Integer> queries, Integer bodyPosition) {
        this.httpMethodType = httpMethodType;
        this.httpMethod = httpMethod;
        this.staticHeaders = staticHeaders;
        this.pathPattern = pathPattern;
        this.bodyType = bodyType;
        this.responseType = responseType;
        this.headers = headers;
        this.paths = paths;
        this.queries = queries;
        this.bodyPosition = bodyPosition;
    }

    public HttpMethodType getHttpMethodType() {
        return httpMethodType;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getStaticHeaders() {
        return staticHeaders;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public Class<?> getBodyType() {
        return bodyType;
    }

    public Class<?> getResponseType() {
        return responseType;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticMetaInfo that = (StaticMetaInfo) o;
        return httpMethodType == that.httpMethodType &&
                httpMethod == that.httpMethod &&
                Objects.equals(staticHeaders, that.staticHeaders) &&
                Objects.equals(pathPattern, that.pathPattern) &&
                Objects.equals(bodyType, that.bodyType) &&
                Objects.equals(responseType, that.responseType) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(paths, that.paths) &&
                Objects.equals(queries, that.queries) &&
                Objects.equals(bodyPosition, that.bodyPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethodType, httpMethod, staticHeaders, pathPattern, bodyType, responseType, headers, paths, queries, bodyPosition);
    }

    @Override
    public String toString() {
        return "StaticMetaInfo{" +
                "httpMethodType=" + httpMethodType +
                ", httpMethod=" + httpMethod +
                ", staticHeaders=" + staticHeaders +
                ", pathPattern='" + pathPattern + '\'' +
                ", bodyType=" + bodyType +
                ", responseType=" + responseType +
                ", headers=" + headers +
                ", paths=" + paths +
                ", queries=" + queries +
                ", bodyPosition=" + bodyPosition +
                '}';
    }
}
