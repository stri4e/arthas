package com.github.arthas.config;

import com.github.arthas.ArthasBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ArthasConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public ArthasBeanPostProcessor arthasBeanPostProcessor(WebClient webClient) {
        return new ArthasBeanPostProcessor(webClient);
    }

}
