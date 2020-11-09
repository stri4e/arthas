package com.github.arthas.decorates.impl;

import com.github.arthas.decorates.MonoResponsePayload;
import com.github.arthas.receivers.ResponseReceiver;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class MonoVoidResponsePayload implements MonoResponsePayload {

    private final ResponseReceiver receiver;

    public MonoVoidResponsePayload(ResponseReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public <V> Mono<V> response(Type type, Method method, Object... params) {
        return this.receiver.request(method, params)
                .responseSingle((resp, content) -> Mono.empty());
    }
}
