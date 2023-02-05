package com.github.processor.factories;

import com.github.processor.annotations.Arthas;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Element;

public interface RESTMethodsFactory {
    MethodSpec method(Element method, Arthas arthas);
}
