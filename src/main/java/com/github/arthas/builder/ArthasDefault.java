package com.github.arthas.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.annotations.ArthasClient;
import com.github.arthas.commands.ICommand;
import com.github.arthas.creators.MethodsContainerCreator;
import com.github.arthas.factory.imp.DecoderFactory;
import com.github.arthas.factory.imp.DecoratorsFactory;
import com.github.arthas.factory.imp.EncoderFactory;
import com.github.arthas.utils.ReflectionUtils;
import io.netty.util.internal.StringUtil;
import reactor.netty.http.client.HttpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ArthasDefault implements Arthas {

    private final Map<String, ICommand> commands = new HashMap<>();

    private String url;

    public Arthas url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public <T> T target(Class<T> clazz) {
        if (clazz.isAnnotationPresent(ArthasClient.class)) {
            ArthasClient arthas = clazz.getAnnotation(ArthasClient.class);
            String tmp = arthas.url();
            if (StringUtil.isNullOrEmpty(tmp) && StringUtil.isNullOrEmpty(this.url)) {
                throw new RuntimeException("U mast set url to ArthasClient annotation or Arthas builder.");
            } else {
                this.url = tmp;
            }
        }
        HttpClient client = HttpClient.create().baseUrl(this.url);
        ObjectMapper mapper = new ObjectMapper();
        MethodsContainerCreator mcc = new MethodsContainerCreator(
                new DecoderFactory(), new EncoderFactory(),
                new DecoratorsFactory(), client, mapper
        );
        Method[] methods = clazz.getMethods();
        Map<String, ICommand> comma = mcc.create(methods);
        this.commands.putAll(comma);
        InvocationHandler handler = handler();
        return ReflectionUtils.newProxy(clazz, handler);
    }

    private InvocationHandler handler() {
        return (proxy, method, params) -> this.commands.get(method.toString())
                .execute(method, params);
    }

}
