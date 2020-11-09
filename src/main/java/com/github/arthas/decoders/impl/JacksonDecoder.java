package com.github.arthas.decoders.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.arthas.decoders.Decoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.ByteBufMono;
import reactor.netty.http.client.HttpClientResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public class JacksonDecoder implements Decoder {

    private final ObjectMapper mapper;

    public JacksonDecoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <V> Mono<V> decode(HttpClientResponse resp, ByteBufMono content, Type type) {
        return content.asString()
                .switchIfEmpty(Mono.empty())
                .map(str -> fromJsonMono(str, type));
    }

    @Override
    public <V> Flux<V> decode(HttpClientResponse resp, ByteBufFlux content, Type type) {
        return content.asString()
                .switchIfEmpty(Flux.empty())
                .flatMapIterable(st -> fromJsonFlux(st, type));
    }

    private  <T> T fromJsonMono(String data, Type type) {
        try {
            JavaType javaType = this.mapper.constructType(type);
            return this.mapper.readValue(data, javaType);
        } catch (IOException e) {
            e.getStackTrace();
        }
        return null;
    }

    private  <T> T fromJsonFlux(String data, Type type) {
        try {
            JavaType javaType = this.mapper.constructType(type);
            if (javaType.isCollectionLikeType()) {
                return this.mapper.readValue(data, javaType);
            }
            CollectionType mainType = mapper.getTypeFactory().
                    constructCollectionType(Collection.class, javaType);
            return this.mapper.readValue(data, mainType);
        } catch (IOException e) {
            e.getStackTrace();
        }
        return null;
    }

}
