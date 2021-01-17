package com.github.arthas.models;

import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;

public interface IStaticMetaInfoBuilder {

    IStaticMetaInfoBuilder method(Method method);

    IStaticMetaInfoBuilder httpMethod(HttpMethod httpMethod);

    IStaticMetaInfoBuilder annotation();

    IStaticMetaInfoBuilder responseTo();

    IStaticMetaInfoBuilder responseToFlux();

    IStaticMetaInfoBuilder responseToMono();

    IStaticMetaInfoBuilder responseToEmptyFlux();

    IStaticMetaInfoBuilder responseToEmptyMono();

    IStaticMetaInfoBuilder methodParams();

    StaticMetaInfo build();

    static IStaticMetaInfoBuilder builder() {
        return new DefaultMetaInfoBuilder();
    }

    static IStaticMetaInfoBuilder scanningBuilder() {
        return new ScanningGenericMetaInfoBuilder();
    }

}
