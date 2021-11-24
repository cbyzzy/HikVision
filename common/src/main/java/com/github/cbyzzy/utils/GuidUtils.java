package com.github.cbyzzy.utils;

import java.util.UUID;

/**
 * Description: 主键生成
 * Author:cbyzzy
 */
public class GuidUtils {

    /**
     * 生成 UUID 主键
     * @return
     */
    public static String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }
}
