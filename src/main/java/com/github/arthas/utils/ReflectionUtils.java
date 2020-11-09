package com.github.arthas.utils;

import com.github.arthas.annotations.Body;
import com.github.arthas.annotations.Header;
import com.github.arthas.annotations.Path;
import com.github.arthas.annotations.Query;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ReflectionUtils {

    public static Map<String, Object> paths(Annotation[][] annotations, Object... params) {
        Map<String, Object> paths = new HashMap<>();
        if (annotations.length != 0) {
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] ann = annotations[i];
                for (Annotation annotation : ann) {
                    if (Path.class.equals(annotation.annotationType())) {
                        Path query = (Path) annotation;
                        String key = query.name();
                        Object value = params[i];
                        paths.put(key, value);
                    }
                }
            }
        }
        return paths;
    }

    public static Map<String, Object> queries(Annotation[][] annotations, Object... params) {
        Map<String, Object> queries = new HashMap<>();
        if (annotations.length != 0) {
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] ann = annotations[i];
                for (Annotation annotation : ann) {
                    if (Query.class.equals(annotation.annotationType())) {
                        Query query = (Query) annotation;
                        String key = query.name();
                        Object value = params[i];
                        queries.put(key, value);
                    }
                }
            }
        }
        return queries;
    }

    public static Object body(Annotation[][] annotations, Object... params) {
        if (annotations.length != 0) {
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] ann = annotations[i];
                for (Annotation annotation : ann) {
                    if (Body.class.equals(annotation.annotationType())) {
                        return params[i];
                    }
                }
            }
        }
        return null;
    }

    public static String bodyTypeName(Annotation[][] annotations, Object... params) {
        if (annotations.length != 0) {
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] ann = annotations[i];
                for (Annotation annotation : ann) {
                    if (Body.class.equals(annotation.annotationType())) {
                        return ((Parameter) params[i]).getType().getName();
                    }
                }
            }
        }
        return null;
    }

    public static Map<String, String> headers(Annotation[][] annotations, Object... params) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] ann = annotations[i];
            for (Annotation annotation : ann) {
                if (Header.class.equals(annotation.annotationType())) {
                    Header header = (Header) annotation;
                    headers.put(header.name(), (String) params[i]);
                }
            }
        }
        return headers;
    }

    public static Type fetchGenericType(Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length != 0) {
                return typeArguments[0];
            }
            throw new RuntimeException("Can't fetch type from reactive types");
        }
        throw new RuntimeException("Can't parameterized types");
    }

    public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler) {
        checkNotNull(handler);
        checkArgument(interfaceType.isInterface(), "%s is not an interface", interfaceType);
        Object object =
                Proxy.newProxyInstance(
                        interfaceType.getClassLoader(), new Class<?>[] {interfaceType}, handler);
        return interfaceType.cast(object);
    }

    public static String fetchSignature(Method method)  {
        try {
            Field f = Method.class.getDeclaredField("signature");
            f.setAccessible(true);
            return (String) f.get(method);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw  new RuntimeException(e.getMessage());
        }
    }

    public static String returnTypeName(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType.getName();
    }

}
