package com.graduation.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ObjectTransfer {

    public static <T> T transfer(Object obj, Class<T> clazz) {
        T t = BeanUtil.copyProperties(obj, clazz);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field: fields) {
            if ("LocalDateTime".equals(field.getType().getSimpleName())) {
                ReflectUtil.setFieldValue(t, field.getName(), DateUtil.formatLocalDateTime((LocalDateTime) ReflectUtil.getFieldValue(obj, field)));
            }
        }
        return t;
    }

}
