package com.github.arthas.decoders;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClientResponse;

import java.lang.reflect.Type;

public interface Decoder {

   <V> Mono<V> decode(HttpClientResponse resp, ByteBufMono content, Type type);

   <V> Flux<V> decode(HttpClientResponse resp, ByteBufFlux content, Type type);

}
