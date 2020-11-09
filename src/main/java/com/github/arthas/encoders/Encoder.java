package com.github.arthas.encoders;

import org.reactivestreams.Publisher;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClientRequest;

import java.util.Map;
import java.util.function.BiFunction;

public interface Encoder {

    BiFunction<? super HttpClientRequest, ? super NettyOutbound, ? extends Publisher<Void>> encode(Object o, Map<String, String> headers);

}
