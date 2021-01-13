package com.github.arthas;

import com.github.arthas.annotations.Arthas;
import com.github.arthas.annotations.BaseMethod;
import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.models.IStaticMetaInfoBuilder;
import com.github.arthas.models.StaticMetaInfo;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.util.*;

public class ArthasBeanPostProcessor implements BeanPostProcessor {

    private final WebClient webClient;

    public ArthasBeanPostProcessor(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object proxy = this.getTargetObject(bean);
        Arthas r = AnnotationUtils.findAnnotation(bean.getClass(), Arthas.class);
        if (r != null) {
            Class<?> target = AopUtils.getTargetClass(proxy);
            Class<?>[] interfaces = target.getInterfaces();
            Method[] methods = Arrays.stream(interfaces)
                    .map(ReflectionUtils::getDeclaredMethods)
                    .flatMap(Arrays::stream)
                    .toArray(Method[]::new);
            Map<String, IHttpMethod> proxyMethods = new HashMap<>();
            Arrays.stream(methods).forEach(m -> {
                BaseMethod bm = AnnotationUtils.findAnnotation(m, BaseMethod.class);
                if (Objects.nonNull(bm)) {
                    StaticMetaInfo i = IStaticMetaInfoBuilder.builder()
                            .method(m)
                            .httpMethod(bm.method())
                            .annotation()
                            .responseToFlux()
                            .responseToMono()
                            .responseToEmptyFlux()
                            .responseToEmptyMono()
                            .methodParams()
                            .build();
                    proxyMethods.put(m.getName(), i.getHttpMethodType().choose(i));
                }
            });
            return proxiedBean(bean, r.url(), proxyMethods);
        }
        return bean;
    }

    private Object proxiedBean(Object bean, String url, Map<String, IHttpMethod> proxyMethods) {
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvice(new ArthasMethodInterceptor(url, this.webClient, proxyMethods));
        return proxyFactory.getProxy();
    }

    private Object getTargetObject(Object proxy) throws BeansException {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            try {
                return ((Advised)proxy).getTargetSource().getTarget();
            } catch (Exception e) {
                throw new FatalBeanException("Error getting target of JDK proxy", e);
            }
        }
        return proxy;
    }

}
