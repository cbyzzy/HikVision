package com.github.cbyzzy.utils;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static String trimAll(String string) {
        if (isBlank(string)) {
            return null;
        }
        return string.replaceAll("\\s+", "");
    }
}
