package com.github.arthas.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.encoders.Encoder;

public interface IEncoderFactory {

    Encoder doDefaultEncoder();

    Encoder doJacksonEncoder(ObjectMapper mapper);

}
