package com.github.cbyzzy.utils;

import com.alibaba.fastjson.*;
import com.alibaba.fastjson.parser.Feature;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    public static <T> T JsonToObject(String json, Class<T> beanClass)
    {
        return (T) JSONObject.parseObject(json, beanClass);
    }

    public static <T> List<T> JsonToArray(String json, Class<T> beanClass)
    {
        return (List<T>) JSONArray.parseArray(json, beanClass);
    }

    public static String ObjectToJson(Object obj)
    {
        return JSONObject.toJSONString(obj);
    }

    public static JSONObject ObjectJson(String json)
    {
        return JSONObject.parseObject(json, Feature.OrderedField);
    }

    public static JSONArray ArrayJson(String json)
    {
        return JSONObject.parseArray(json);
    }
}
