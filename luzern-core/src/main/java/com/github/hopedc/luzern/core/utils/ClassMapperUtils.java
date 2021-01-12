package com.github.hopedc.luzern.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hopedc
 * @date 2017/4/3 0003
 */
public class ClassMapperUtils {

    private static Map<String, String> classPath = new HashMap<>();

    public static void put(String name, String path) {
        classPath.put(name, path);
    }

    public static String getPath(String name) {
        return classPath.get(name);
    }
}
