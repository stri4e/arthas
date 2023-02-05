package com.github.processor.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;

public class TypesUtils {

    public static boolean isMultiValueMap(TypeMirror type) {
        TypeName typeToCheck = ClassName.get(type);
        String strType = typeToCheck.toString();
        return strType.contains("org.springframework.util.MultiValueMap");
    }

    public static boolean isOptional(TypeMirror type) {
        TypeName typeToCheck = ClassName.get(type);
        String strType = typeToCheck.toString();
        return strType.contains("java.util.Optional");
    }

}
