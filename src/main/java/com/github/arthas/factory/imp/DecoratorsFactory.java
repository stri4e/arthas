package com.github.arthas.factory.imp;

import com.github.arthas.decoders.Decoder;
import com.github.arthas.decorates.FluxResponsePayload;
import com.github.arthas.decorates.MonoResponsePayload;
import com.github.arthas.decorates.impl.FluxJsonResponsePayload;
import com.github.arthas.decorates.impl.MonoJsonResponsePayload;
import com.github.arthas.decorates.impl.MonoVoidResponsePayload;
import com.github.arthas.encoders.Encoder;
import com.github.arthas.endpoints.impl.EndpointMethods;
import com.github.arthas.endpoints.impl.EndpointUrl;
import com.github.arthas.factory.IDecoratorsFactory;
import com.github.arthas.receivers.impl.DefaultSend;
import io.netty.handler.codec.http.HttpMethod;
import reactor.netty.http.client.HttpClient;

public class DecoratorsFactory implements IDecoratorsFactory {

    @Override
    public MonoResponsePayload
    doMonoObj(HttpClient client, Decoder decoder, Encoder encoder, HttpMethod method, String pattern) {
        return new MonoJsonResponsePayload(
                decoder,
                new DefaultSend(
                        new EndpointUrl(
                                new EndpointMethods(client, method),
                                pattern
                        ), encoder
                )
        );
    }

    @Override
    public MonoResponsePayload doMonoVoid(HttpClient client, Encoder encoder, HttpMethod method, String pattern) {
        return new MonoVoidResponsePayload(
                new DefaultSend(
                        new EndpointUrl(
                                new EndpointMethods(client, method),
                                pattern
                        ), encoder
                )
        );
    }

    @Override
    public FluxResponsePayload doFluxObj(HttpClient client, Decoder decoder, Encoder encoder, HttpMethod method, String pattern) {
        return new FluxJsonResponsePayload(
                decoder,
                new DefaultSend(new EndpointUrl(
                        new EndpointMethods(client, method),
                        pattern
                ),
                        encoder
                )
        );
    }

}
