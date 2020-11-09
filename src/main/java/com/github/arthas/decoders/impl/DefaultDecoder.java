package com.github.arthas.decoders.impl;

import com.github.arthas.decoders.Decoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClientResponse;

import java.lang.reflect.Type;

public class DefaultDecoder implements Decoder {

    @Override
    @SuppressWarnings(value = "unchecked")
    public Mono<String> decode(HttpClientResponse resp, ByteBufMono content, Type type) {
        return content.asString();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Flux<String> decode(HttpClientResponse resp, ByteBufFlux content, Type type) {
        return content.asString();
    }
}
