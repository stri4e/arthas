package com.github.arthas.receivers;

import reactor.netty.http.client.HttpClient;

import java.lang.reflect.Method;

public interface ResponseReceiver {

    HttpClient.ResponseReceiver<?> request(Method method, Object... params);

}
