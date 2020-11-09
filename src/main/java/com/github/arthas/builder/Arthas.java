package com.github.arthas.builder;

public interface Arthas {

    <T> T target(Class<T> clazz);

    static Arthas builder() {
        return new ArthasDefault();
    }

}
