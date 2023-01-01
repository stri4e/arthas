package com.github.arthas;

import com.github.arthas.annotations.Arthas;
import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.models.StaticMetaInfo;
import com.github.arthas.utils.ArthasMetaInfoHandler;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.util.*;
import static java.util.stream.Collectors.toMap;

public class ArthasAnnotationBeanPostProcessor implements BeanPostProcessor, DestructionAwareBeanPostProcessor {

    private final WebClient webClient;

    private final Map<String, Class<?>> arthasObjects = new HashMap<>();

    public ArthasAnnotationBeanPostProcessor(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arthas arthas = AnnotationUtils.findAnnotation(bean.getClass(), Arthas.class);
        if (Objects.nonNull(arthas)) {
            this.arthasObjects.put(beanName, AopUtils.getTargetClass(bean));
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> target = this.arthasObjects.get(beanName);
        if (Objects.nonNull(target)) {
            Arthas arthas = AnnotationUtils.findAnnotation(bean.getClass(), Arthas.class);
            if (Objects.isNull(arthas)) {
                throw new RuntimeException("Can't find Arthas annotation in class declaration!");
            }
            Class<?>[] interfaces = target.getInterfaces();
            Method[] methods = Arrays.stream(interfaces)
                    .map(ReflectionUtils::getDeclaredMethods)
                    .flatMap(Arrays::stream)
                    .toArray(Method[]::new);
            Map<String, IHttpMethod> proxyMethods = Arrays.stream(methods)
                    .map(ArthasMetaInfoHandler::generateMetaInfo)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toMap(
                            StaticMetaInfo::getMethodName,
                            staticMetaInfo -> staticMetaInfo.getHttpMethodType().choose(staticMetaInfo)
                    ));
            return proxiedBean(bean, arthas.url(), proxyMethods);
        }
        return bean;
    }

    private Object proxiedBean(Object bean, String url, Map<String, IHttpMethod> proxyMethods) {
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvice(new ArthasMethodInterceptor(url, this.webClient, proxyMethods));
        return proxyFactory.getProxy();
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        System.out.println("Destroy bean " + beanName);
    }
}
