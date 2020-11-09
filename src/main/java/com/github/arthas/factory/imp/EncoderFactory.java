package com.github.arthas.factory.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.encoders.Encoder;
import com.github.arthas.encoders.imp.DefaultEncoder;
import com.github.arthas.encoders.imp.JacksonEncoder;
import com.github.arthas.factory.IEncoderFactory;

public class EncoderFactory implements IEncoderFactory {

    @Override
    public Encoder doDefaultEncoder() {
        return new DefaultEncoder();
    }

    @Override
    public Encoder doJacksonEncoder(ObjectMapper mapper) {
        return new JacksonEncoder(mapper);
    }
}
