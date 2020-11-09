package com.github.arthas.endpoints.impl;

import com.github.arthas.endpoints.IEndpointAttributes;
import io.netty.handler.codec.http.HttpMethod;
import reactor.netty.http.client.HttpClient;

import java.lang.reflect.Method;

public final class EndpointMethods implements IEndpointAttributes {

    private final HttpClient client;

    private final HttpMethod httpMethod;

    public EndpointMethods(HttpClient client, HttpMethod httpMethod) {
        this.client = client;
        this.httpMethod = httpMethod;
    }

    @Override
    public HttpClient.RequestSender request(Method method, Object... params) {
        return this.client.request(this.httpMethod);
    }

}
