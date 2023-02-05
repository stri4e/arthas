package com.github.processor.utils;

import com.squareup.javapoet.ClassName;

public class SpringTypeSpecifications {

    public static ClassName webClient() {
        return ClassName.get("org.springframework.web.reactive.function.client", "WebClient");
    }

    public static ClassName parameterizedTypeReference() {
        return ClassName.get("org.springframework.core", "ParameterizedTypeReference");
    }

    public static ClassName mono() {
        return ClassName.get("reactor.core.publisher", "Mono");
    }

    public static ClassName flux() {
        return ClassName.get("reactor.core.publisher", "Flux");
    }

    public static ClassName responseEntity() {
        return ClassName.get("org.springframework.http", "ResponseEntity");
    }

    public static ClassName webClientBuilder() {
        return ClassName.get("org.springframework.web.reactive.function.client", "WebClient.Builder");
    }

    public static ClassName mediaType() {
        return ClassName.get("org.springframework.http", "MediaType");
    }

    public static ClassName uriComponentsBuilder() {
        return ClassName.get("org.springframework.web.util", "UriComponentsBuilder");
    }

}
