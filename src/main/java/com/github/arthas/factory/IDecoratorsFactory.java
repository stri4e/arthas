package com.github.arthas.factory;

import com.github.arthas.decoders.Decoder;
import com.github.arthas.decorates.FluxResponsePayload;
import com.github.arthas.decorates.MonoResponsePayload;
import com.github.arthas.encoders.Encoder;
import io.netty.handler.codec.http.HttpMethod;
import reactor.netty.http.client.HttpClient;

public interface IDecoratorsFactory {

    MonoResponsePayload doMonoObj(
            HttpClient client, Decoder decoder,
            Encoder encoder, HttpMethod method, String pattern
    );

    MonoResponsePayload doMonoVoid(
            HttpClient client, Encoder encoder,
            HttpMethod method, String pattern
    );

    FluxResponsePayload doFluxObj(
            HttpClient client, Decoder decoder, Encoder encoder,
            HttpMethod method, String pattern
    );

}
