package com.hthyaq.salaryadmin.util;

import java.util.List;

//对象的相同属性被覆盖，主要用于批量修改的内聘、离退休工资
public class CoverObj {
    private static final ThreadLocal<CoverObj> THREAD_LOCAL = ThreadLocal.withInitial(() -> new CoverObj());

    public static void cover(ReflectUtil reflectUtil, Object source, Object target, String... ignoreMethodName) {
        THREAD_LOCAL.get().handle(reflectUtil, source, target, ignoreMethodName);
    }

    public static void cover(ReflectUtil reflectUtil, Object source, Object target) {
        THREAD_LOCAL.get().handle(reflectUtil, source, target, null);
    }

    private void handle(ReflectUtil reflectUtil, Object source, Object target, String... ignoreMethodName) {
        Class sourceClass = source.getClass();
        List<String> sourceMethodName = null;
        if (ignoreMethodName != null) {
            sourceMethodName = reflectUtil.getMethodNames(sourceClass, ignoreMethodName);
        } else {
            sourceMethodName = reflectUtil.getMethodNames(sourceClass);
        }
        Object sourceValue = null;
        for (String name : sourceMethodName) {
            if (name.startsWith("set")) {
                //取出source中的值
                sourceValue = reflectUtil.invokeReturnValue(source, name.replace("set", "get"));
                if (sourceValue != null) {
                    //给target设置值
                    reflectUtil.invoke(target, name, sourceValue);
                }
            }
        }
    }

}
