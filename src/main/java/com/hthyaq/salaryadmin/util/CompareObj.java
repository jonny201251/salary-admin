package com.hthyaq.salaryadmin.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CompareObj {
    private static final ThreadLocal<CompareObj> THREAD_LOCAL = ThreadLocal.withInitial(() -> new CompareObj());

    /**
     * @param excludeProperty 排除不需要比较的属性，例如，id,createTime
     */
    public static <T> List<CompareObjResult> get(T oldObj, T newObj, String excludeProperty) {
        return THREAD_LOCAL.get().handle(oldObj, newObj, excludeProperty);
    }

    public static <T> List<CompareObjResult> get(T oldObj, T newObj) {
        return THREAD_LOCAL.get().handle(oldObj, newObj, "");
    }

    private <T> List<CompareObjResult> handle(T oldObj, T newObj, String excludeProperty) {
        Set<String> excludeProperties = new HashSet<>(Arrays.asList(excludeProperty.split(",")));
        List<CompareObjResult> list = new ArrayList<>();
        String fieldName = "";
        try {
            Class clazz = oldObj.getClass();
            Field[] fields = oldObj.getClass().getDeclaredFields();
            for (Field field : fields) {
                fieldName = field.getName();
                if ("serialVersionUID".equals(fieldName) || excludeProperties.contains(fieldName)) continue;
                PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
                Method getMethod = pd.getReadMethod();
                Object oldValue = getMethod.invoke(oldObj);
                Object newValue = getMethod.invoke(newObj);
                if (oldValue == null || newValue == null) continue;
                if (oldValue instanceof Double) {
                    if (!oldValue.toString().equals(newValue.toString())) {
                        list.add(new CompareObjResult(fieldName, oldValue, newValue));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return list;
    }
}
