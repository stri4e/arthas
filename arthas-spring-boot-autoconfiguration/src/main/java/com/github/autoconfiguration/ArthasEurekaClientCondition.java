package com.github.autoconfiguration;

import com.github.processor.annotations.EnableArthasDiscoveryClient;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.Objects;

public class ArthasEurekaClientCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        if (Objects.nonNull(beanFactory)) {
            Map<String, Object> bean = beanFactory.getBeansWithAnnotation(EnableArthasDiscoveryClient.class);
            return !bean.isEmpty();
        }
        return false;
    }

}
