package com.github.arthas.encoders.imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.encoders.Encoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class JacksonEncoder implements Encoder {

    private final ObjectMapper mapper;

    public JacksonEncoder(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BiFunction<? super HttpClientRequest, ? super NettyOutbound, ? extends Publisher<Void>> encode(Object body, Map<String, String> headers) {
        return (httpRequest, out) -> {
            httpRequest.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            headers.keySet().forEach(k -> httpRequest.addHeader(k, headers.get(k)));
            return out.send(ByteBufFlux.fromString(Flux.just(Objects.requireNonNull(toJson(body)))));
        };
    }

    private String toJson(Object o) {
        try {
            return this.mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
