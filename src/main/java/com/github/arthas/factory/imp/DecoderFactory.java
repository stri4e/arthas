package com.github.arthas.factory.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.decoders.Decoder;
import com.github.arthas.decoders.impl.DefaultDecoder;
import com.github.arthas.decoders.impl.JacksonDecoder;
import com.github.arthas.factory.IDecoderFactory;

public class DecoderFactory implements IDecoderFactory {

    @Override
    public Decoder doDefaultDecoder() {
        return new DefaultDecoder();
    }

    @Override
    public Decoder doJsonDecoder(ObjectMapper mapper) {
        return new JacksonDecoder(mapper);
    }

}
