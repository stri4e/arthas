package com.github.arthas.endpoints.impl;

import com.github.arthas.endpoints.IEndpointAttributes;
import com.github.arthas.utils.ReflectionUtils;
import com.github.arthas.utils.UriTemplate;
import reactor.netty.http.client.HttpClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

public final class EndpointUrl implements IEndpointAttributes {

    private final IEndpointAttributes request;

    private final String pattern;

    public EndpointUrl(IEndpointAttributes request, String pattern) {
        this.request = request;
        this.pattern = pattern;
    }

    @Override
    public HttpClient.RequestSender request(Method method, Object... params) {
        return request(this.request.request(method, params), method, params);
    }

    public HttpClient.RequestSender request(HttpClient.RequestSender sender, Method method, Object... params) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Map<String, Object> paths = ReflectionUtils.paths(annotations, params);
        Map<String, Object> queries = ReflectionUtils.queries(annotations, params);
        URI uri = UriTemplate.collectUri(this.pattern, paths, queries);
        return sender.uri(uri.toString());
    }

}
