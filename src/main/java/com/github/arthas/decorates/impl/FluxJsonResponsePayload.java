package com.github.arthas.decorates.impl;

import com.github.arthas.decoders.Decoder;
import com.github.arthas.decorates.FluxResponsePayload;
import com.github.arthas.receivers.ResponseReceiver;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class FluxJsonResponsePayload implements FluxResponsePayload {

    private final Decoder decoder;

    private final ResponseReceiver receiver;

    public FluxJsonResponsePayload(Decoder decoder, ResponseReceiver receiver) {
        this.decoder = decoder;
        this.receiver = receiver;
    }

    @Override
    public <V> Flux<V> response(Type type, Method method, Object... params) {
        return this.receiver.request(method, params)
                .response((resp, content) -> this.decoder.decode(resp, content, type));
    }

}
