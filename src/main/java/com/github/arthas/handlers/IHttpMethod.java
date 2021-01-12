package com.github.arthas.handlers;

import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Parameter;

public interface IHttpMethod {

    Object method(WebClient webClient, String baseUri, Object[] arguments, Parameter[] params);

}
