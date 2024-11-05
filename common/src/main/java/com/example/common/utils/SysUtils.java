package com.example.common.utils;

public class SysUtils {
    /**
     * 判断给定的类是否是 Kotlin 类
     *
     * @param clazz 要检查的类
     * @return 如果是 Kotlin 类返回 true，否则返回 false
     */
    public static boolean isKt(Class<?> clazz) {
        // 检查类名是否以 "kotlin" 开头
        return clazz.getName().startsWith("kotlin");
    }
}
