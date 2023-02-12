package com.github.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ArthasConfiguration {

    @Bean
    @Order(value = 0)
    @LoadBalanced
    @ConditionalOnMissingBean(WebClient.Builder.class)
    @Conditional(value = ArthasEurekaClientCondition.class)
    public WebClient.Builder webClientWithLoadBalanced(ApplicationContext context) {
        return WebClient.builder()
                .filter(context.getBean(ReactorLoadBalancerExchangeFilterFunction.class));
    }

    @Bean
    @Order(value = 1)
    @ConditionalOnMissingBean(WebClient.Builder.class)
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }

}
