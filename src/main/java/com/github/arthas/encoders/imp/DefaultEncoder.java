package com.github.arthas.encoders.imp;

import com.github.arthas.encoders.Encoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class DefaultEncoder implements Encoder {

    @Override
    public BiFunction<? super HttpClientRequest, ? super NettyOutbound, ? extends Publisher<Void>> encode(Object payload, Map<String, String> headers) {
        return (httpRequest, out) -> {
            headers.keySet().forEach(k -> httpRequest.addHeader(k, headers.get(k)));
            if (Objects.nonNull(payload)) {
                httpRequest.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
                return out.sendString(Mono.just(String.valueOf(payload)));
            }
            return out.then();
        };
    }

}
