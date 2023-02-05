package com.github.processor.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static <T> T getValFromAttributeConstant(Object value, Class<T> toCast) {
        try {
            Class<?> clz = value.getClass();
            Field fVal = clz.getDeclaredField("value");
            fVal.setAccessible(Boolean.TRUE);
            return toCast.cast(fVal.get(value));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
