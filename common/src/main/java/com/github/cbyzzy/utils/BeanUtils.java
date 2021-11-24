package com.github.cbyzzy.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Description:
 * Author:cbyzzy
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 类型转换
     * @param t
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T convert(T t, Object obj){
        copyProperties(obj, t);
        return t;
    }

    /**
     * map转java对象
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object object = beanClass.newInstance();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
                continue;
            }
            field.setAccessible(true);
            field.set(object, map.get(field.getName()));
        }
        return object;
    }

    /**
     * java对象转map
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

}

