package com.github.arthas.decorates;

import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface FluxResponsePayload {

    <V> Flux<V> response(Type type, Method method, Object... params);

}
