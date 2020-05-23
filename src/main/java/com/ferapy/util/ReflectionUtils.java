package com.ferapy.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.nonNull;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {
    private static final ConcurrentMap<Class<?>, Field[]> declaredFields = new ConcurrentHashMap<>();

    public static Field[] getDeclaredFields(Class<?> clazz) {
        return declaredFields.computeIfAbsent(clazz, Class::getDeclaredFields);
    }

    public static void setPropertyValue(Object obj, String propertyName, Object value) {
        final PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName);
        try {
            if (nonNull(descriptor)) {
                descriptor.getWriteMethod().invoke(obj, new Object[]{value});
            }
        } catch (Exception e) {
            log.warn("Error occurred on writing the field value", e);
        }
    }

    public static Object getPropertyValue(Object obj, String propertyName) {
        final PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName);
        try {
            return nonNull(descriptor) ? descriptor.getReadMethod().invoke(obj) : null;
        } catch (Exception e) {
            log.error("Error occurred on getting the field value", e);
            return null;
        }
    }

}
