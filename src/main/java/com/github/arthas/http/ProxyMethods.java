package com.github.arthas.http;

import com.github.arthas.models.StaticMetaInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

public class ProxyMethods {

    public static Function<StaticMetaInfo, Object> onlyMono(WebClient webClient, Object body, URI uri, Map<String, String> headers) {
        return mi -> webClient
                .method(mi.getHttpMethod())
                .uri(uri)
                .headers(hs -> hs.setAll(headers))
                .body(Mono.justOrEmpty(body), ParameterizedTypeReference.forType(mi.getBodyType()))
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(mi.getResponseType()));
    }

    public static Function<StaticMetaInfo, Object> onlyFlux(WebClient webClient, Object body, URI uri, Map<String, String> headers) {
        return mi -> webClient
                .method(mi.getHttpMethod())
                .uri(uri)
                .headers(hs -> hs.setAll(headers))
                .body(Flux.just(body), ParameterizedTypeReference.forType(mi.getBodyType()))
                .retrieve()
                .bodyToFlux(ParameterizedTypeReference.forType(mi.getResponseType()));
    }

    public static Function<StaticMetaInfo, Object> monoWithoutBody(WebClient webClient, URI uri, Map<String, String> headers) {
        return mi -> webClient
                .method(mi.getHttpMethod())
                .uri(uri)
                .headers(hs -> hs.setAll(headers))
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(mi.getResponseType()));
    }

    public static Function<StaticMetaInfo, Object> fluxWithoutBody(WebClient webClient, URI uri, Map<String, String> headers) {
        return mi -> webClient
                .method(mi.getHttpMethod())
                .uri(uri)
                .headers(hs -> hs.setAll(headers))
                .retrieve()
                .bodyToFlux(ParameterizedTypeReference.forType(mi.getResponseType()));
    }

    public static Function<StaticMetaInfo, Object> bodyMonoRespFlux(WebClient webClient, Object body, URI uri, Map<String, String> headers) {
        return mi -> webClient
                .method(mi.getHttpMethod())
                .uri(uri)
                .headers(hs -> hs.setAll(headers))
                .body(Mono.justOrEmpty(body), ParameterizedTypeReference.forType(mi.getBodyType()))
                .retrieve()
                .bodyToFlux(ParameterizedTypeReference.forType(mi.getResponseType()));
    }

    public static Function<StaticMetaInfo, Object> bodyFluxRespMono(WebClient webClient, Object body, URI uri, Map<String, String> headers) {
        return mi -> webClient
                .method(mi.getHttpMethod())
                .uri(uri)
                .headers(hs -> hs.setAll(headers))
                .body(Flux.just(body), ParameterizedTypeReference.forType(mi.getBodyType()))
                .retrieve()
                .bodyToMono(ParameterizedTypeReference.forType(mi.getResponseType()));
    }

}
