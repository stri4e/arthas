package com.github.arthas.decorates;

import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public interface MonoResponsePayload {

    <V> Mono<V> response(Type type, Method method, Object... params);

}
