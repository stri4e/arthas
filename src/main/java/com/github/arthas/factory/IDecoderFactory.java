package com.github.arthas.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.decoders.Decoder;

public interface IDecoderFactory {

    Decoder doDefaultDecoder();

    Decoder doJsonDecoder(ObjectMapper mapper);

}
