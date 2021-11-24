package com.github.cbyzzy.utils;

import com.github.cbyzzy.exception.UnAuthorizedException;
import com.github.cbyzzy.exception.WebException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 常用属性检查
 * Author:cbyzzy
 */
public class HAssert {

    /**
     * 库表名称合法性
     */
    public static final String checkDbName = "^[_a-zA-Z][_a-zA-Z0-9.]*$";

    /**
     * 判断字符串是否为空
     * @param str 字符串
     * @param msg 为空提示信息
     */
    public static void isBlank(String str, String msg) {
        if (StringUtils.isBlank(str)) {
            throw new WebException(msg);
        }
    }

    /**
     * 判断对象是否为空
     * @param obj 对象
     * @param msg 为空提示信息
     */
    public static void isBlank(Object obj, String msg) {
        if (obj instanceof String) {
            isBlank((String) obj, msg);
        }
        if (obj == null) {
            throw new WebException(msg);
        }
    }

    /**
     * 如果条件为true, 异常提醒
     * @param b
     * @param msg
     */
    public static void isTrue(Boolean b, String msg) {
        if (b) {
            throw new WebException(msg);
        }
    }

    /**
     * 登录验证
     * @param b
     */
    public static void unAuth(Boolean b, String msg) {
        if (b) {
            throw new UnAuthorizedException(msg);
        }
    }

    /**
     * 异常提醒
     * @param msg
     */
    public static void error(String msg) {
        throw new WebException(msg);
    }

    /**
     * 表名合法性验证
     * @param str
     * @return
     */
    public static boolean check(String str) {
        Pattern p = Pattern.compile(checkDbName);
        Matcher matcher = p.matcher(str);
        return matcher.matches();
    }

}
