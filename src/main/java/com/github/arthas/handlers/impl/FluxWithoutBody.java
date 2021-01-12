package com.github.arthas.handlers.impl;

import com.github.arthas.http.ProxyMethods;
import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.models.DynamicMetaInfo;
import com.github.arthas.models.StaticMetaInfo;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.arthas.utils.ReflectParamsUtils.uri;

public final class FluxWithoutBody implements IHttpMethod {

    private final StaticMetaInfo methodMetaInfo;

    private final DynamicMetaInfo dynamic = new DynamicMetaInfo();

    public FluxWithoutBody(StaticMetaInfo methodMetaInfo) {
        this.methodMetaInfo = methodMetaInfo;
    }

    @Override
    public Object method(WebClient webClient, String baseUri, Object[] arguments, Parameter[] params) {
        if (this.dynamic.isEmpty()) {
            this.dynamic.collect(params);
        }
        Map<String, Integer> rawHeaders = this.dynamic.getHeaders();
        Map<String, String> headers = this.methodMetaInfo.getStaticHeaders();
        headers.putAll(rawHeaders.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(), k -> (String) arguments[rawHeaders.get(k)])
                ));
        return ProxyMethods.fluxWithoutBody(
                webClient,
                uri(
                        baseUri,
                        this.methodMetaInfo.getPathPattern(),
                        this.dynamic,
                        arguments
                ),
                headers
        ).apply(this.methodMetaInfo);
    }

}
