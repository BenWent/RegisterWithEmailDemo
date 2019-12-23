package fhq.demo.util;

import com.alibaba.fastjson.JSON;

/**
 * @author happy
 * @date 2019/9/29
 */
public class StrObjTransformerUtil {
    /**
     * 将类对象转换为字符串
     *
     * @param value 类对象
     * @param <T>
     * @return 类对象对应的字符串
     */
    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();

        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    /**
     * 将 字符串转为指定类型的类对象
     *
     * @param s     待转换为类对象的字符串
     * @param clazz 指定转变为何种类型
     * @param <T>
     * @return 类对象
     */
    public static <T> T stringToBean(String s, Class<T> clazz) {
        if (s == null || s.length() <= 0) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(s);
        } else if (clazz == String.class) {
            return (T) s;
        } else {
            return JSON.toJavaObject(JSON.parseObject(s), clazz);
        }
    }
}
