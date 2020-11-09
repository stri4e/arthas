package com.github.arthas.utils;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class UriTemplate {

    private UriTemplate() {
    }

    public static String replacePaths(String pattern, Map<String, Object> paths) {
        Set<String> patterns = paths.keySet();
        for (String str : patterns) {
            pattern = pattern.replace(decorator(str), paths.get(str).toString());
        }
        return pattern;
    }

    public static String query(Map<String, Object> queries) {
        return queries
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    public static URI collectUri(String base, String pattern, Map<String, Object> paths, Map<String, Object> queries) {
        String tmp = base.concat(UriTemplate.replacePaths(pattern, paths));
        if (!queries.isEmpty()) {
            tmp = tmp.concat("?").concat(query(queries));
        }
        return URI.create(tmp);
    }

    public static URI collectUri( String pattern, Map<String, Object> paths, Map<String, Object> queries) {
        String result = UriTemplate.replacePaths(pattern, paths);
        if (!queries.isEmpty()) {
            result = result.concat("?").concat(query(queries));
        }
        return URI.create(result);
    }

    private static String decorator(String str) {
        return "{" + str + "}";
    }

}
