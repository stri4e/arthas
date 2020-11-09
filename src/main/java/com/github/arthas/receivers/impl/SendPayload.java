package com.github.arthas.receivers.impl;

import com.github.arthas.encoders.Encoder;
import com.github.arthas.endpoints.IEndpointAttributes;
import com.github.arthas.receivers.ResponseReceiver;
import com.github.arthas.utils.ReflectionUtils;
import reactor.netty.http.client.HttpClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public final class SendPayload implements ResponseReceiver {

    private final IEndpointAttributes request;

    private final Encoder encoder;

    public SendPayload(IEndpointAttributes request, Encoder encoder) {
        this.request = request;
        this.encoder = encoder;
    }

    @Override
    public HttpClient.ResponseReceiver<?> request(Method method, Object... params) {
        return request(this.request.request(method, params), method, params);
    }

    public HttpClient.ResponseReceiver<?> request(HttpClient.RequestSender sender, Method method, Object... params) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Object body = ReflectionUtils.body(annotations, params);
        Map<String, String> headers = ReflectionUtils.headers(annotations, params);
        return sender.send(this.encoder.encode(body, headers));
    }

}
