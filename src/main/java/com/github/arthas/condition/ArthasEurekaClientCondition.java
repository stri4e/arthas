package com.github.arthas.condition;

import com.github.arthas.annotations.EnableArthasEurekaClient;
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
            Map<String, Object> bean = beanFactory.getBeansWithAnnotation(EnableArthasEurekaClient.class);
            return !bean.isEmpty();
        }
        return false;
    }

}
