package com.github.arthas.utils;

import com.github.arthas.annotations.Body;
import com.github.arthas.annotations.Header;
import com.github.arthas.annotations.Path;
import com.github.arthas.annotations.Query;
import com.github.arthas.models.StaticMetaInfo;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectParamsUtils {

    public static URI uri(String baseUri, String pathPattern, StaticMetaInfo methodMetaInfo, Object[] arguments) {
        Map<String, Integer> paths = methodMetaInfo.getPaths();
        Map<String, Integer> queries = methodMetaInfo.getQueries();
        return UriComponentsBuilder.fromUriString(baseUri)
                .path(pathPattern)
                .query(queries.entrySet().stream()
                        .map(e -> aggregateQuery(e.getKey(), arguments[e.getValue()]))
                        .collect(Collectors.joining("&")))
                .uriVariables(paths.keySet().stream()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                k -> arguments[paths.get(k)]
                        ))).build().toUri();
    }

    private static String aggregateQuery(Object key, Object value) {
        String result = "";
        if (value instanceof Collection) {
           result = key + "=" + ((Collection<?>) value).stream().map(String::valueOf)
                    .collect(Collectors.joining(","));
        } else if (value.getClass().isArray()) {
            result = key + "=" + Stream.of(value).map(String::valueOf)
                    .collect(Collectors.joining(","));
        } else {
            result = key + "=" + value;
        }
        return result;
    }


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
