package com.github.arthas.config;

import com.github.arthas.ArthasAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryHealthIndicatorEnabled;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ArthasConfig {

    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public ArthasConfig(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }

    @Bean
    @Order(value = 0)
    @LoadBalanced
    @ConditionalOnDiscoveryHealthIndicatorEnabled
    public WebClient webClientWithLoadBalanced() {
        return WebClient.builder()
                .filter(this.lbFunction)
                .build();
    }

    @Bean
    @Order(value = 1)
    @ConditionalOnMissingBean(WebClient.class)
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public ArthasAnnotationBeanPostProcessor arthasBeanPostProcessor(WebClient webClient) {
        return new ArthasAnnotationBeanPostProcessor(webClient);
    }

}
