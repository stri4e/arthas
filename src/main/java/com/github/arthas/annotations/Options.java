package com.github.arthas.annotations;

import org.springframework.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@BaseMethod(method = HttpMethod.OPTIONS)
public @interface Options {

    String path() default "";

    Header[] headers() default {};

}
