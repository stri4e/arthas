package com.github.arthas.endpoints;

import reactor.netty.http.client.HttpClient;

import java.lang.reflect.Method;

public interface IEndpointAttributes {

    HttpClient.RequestSender request(Method method, Object... params);

}
