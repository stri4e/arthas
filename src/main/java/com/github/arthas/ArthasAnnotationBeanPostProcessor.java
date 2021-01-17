package com.github.arthas;

import com.github.arthas.annotations.Arthas;
import com.github.arthas.annotations.BaseMethod;
import com.github.arthas.annotations.BodyToFlux;
import com.github.arthas.annotations.BodyToMono;
import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.models.IStaticMetaInfoBuilder;
import com.github.arthas.models.StaticMetaInfo;
import com.github.arthas.utils.ArthasAnnotationUtils;
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

import static com.github.arthas.utils.ArthasAnnotationUtils.isOnePresent;

public class ArthasAnnotationBeanPostProcessor implements BeanPostProcessor {

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
            Arthas arthas = target.getAnnotation(Arthas.class);
            Class<?>[] interfaces = target.getInterfaces();
            Method[] methods = Arrays.stream(interfaces)
                    .map(ReflectionUtils::getDeclaredMethods)
                    .flatMap(Arrays::stream)
                    .toArray(Method[]::new);
            Map<String, IHttpMethod> proxyMethods = new HashMap<>();
            Arrays.stream(methods).forEach(m -> {
                BaseMethod bm = AnnotationUtils.findAnnotation(m, BaseMethod.class);
                if (Objects.nonNull(bm)) {
                    if (isOnePresent(m)) {
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
                    } else {
                        StaticMetaInfo i = IStaticMetaInfoBuilder.scanningBuilder()
                                .method(m)
                                .httpMethod(bm.method())
                                .annotation()
                                .responseTo()
                                .methodParams()
                                .build();
                        proxyMethods.put(m.getName(), i.getHttpMethodType().choose(i));
                    }
                }
            });
            return proxiedBean(bean, arthas.url(), proxyMethods);
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
