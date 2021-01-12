package com.github.arthas;

import com.github.arthas.annotations.Arthas;
import com.github.arthas.annotations.BaseMethod;
import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.http.AnnotationProcessor;
import com.github.arthas.models.StaticMetaInfo;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            System.out.println(r.url());
            System.out.println(AopUtils.isJdkDynamicProxy(proxy));
            Class<?> target = AopUtils.getTargetClass(proxy);
            Method[] methods = target.getMethods();
            Map<String, IHttpMethod> proxyMethods = new HashMap<>();
            Arrays.stream(methods).forEach(m -> {
                BaseMethod btn = AnnotationUtils.findAnnotation(m, BaseMethod.class);
                if (Objects.nonNull(btn)) {
                    StaticMetaInfo i = AnnotationProcessor.staticMetaInfo(m, AnnotationProcessor.chooseAnnotation(btn.method()), btn.method());
                    proxyMethods.put(m.getName(), i.getHttpMethodType().choose(i));
                }
            });
            return proxiedBean(bean, r.url(), proxyMethods);
        }
        Arthas annotation = AnnotationUtils.getAnnotation(proxy.getClass(), Arthas.class);
        if (annotation != null) {
            return bean;
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
