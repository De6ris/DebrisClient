package com.github.Debris.DebrisClient.util;

import java.util.List;

public class GenericsUtil {
    @SuppressWarnings("unchecked")
    public static <T> List<T> cast(List<?> list) {
        return (List<T>) list;
    }
}
