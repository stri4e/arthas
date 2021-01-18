package com.github.arthas;

import com.github.arthas.handlers.IHttpMethod;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class ArthasMethodInterceptor implements MethodInterceptor {

    private final String url;

    private final WebClient webClient;

    private final Map<String, IHttpMethod> proxyMethods;

    public ArthasMethodInterceptor(String url, WebClient webClient, Map<String, IHttpMethod> proxyMethods) {
        this.url = url;
        this.webClient = webClient;
        this.proxyMethods = proxyMethods;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        Parameter[] params = method.getParameters();
        return this.proxyMethods.get(method.toString())
                .method(this.webClient, this.url, arguments, params);
    }

}
