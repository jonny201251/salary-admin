package com.hthyaq.salaryadmin.util;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectUtil {

    //获取被public修饰的方法名
    public List<String> getMethodNames(Class clazz) {
        List<String> methodNames = Lists.newArrayList();
        Method[] methods = clazz.getMethods();
        Class tmp = null;
        for (Method method : methods) {
            Class[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                tmp = parameterTypes[i];
                if (tmp == Double.class) methodNames.add(method.getName());
            }
        }
        return methodNames;
    }

    //获取被public修饰的方法名,忽略方法名
    public List<String> getMethodNames(Class clazz, String... ignoreMethodName) {
        List<String> require = Lists.newArrayList();
        List<String> names = getMethodNames(clazz);
        Set<String> ignoreMethodNames = new HashSet<>(Arrays.asList(ignoreMethodName));
        names.forEach(name -> {
            if (!ignoreMethodNames.contains(name)) {
                require.add(name);
            }
        });
        return require;
    }

    //调用有参的方法
    public void invoke(Object obj, String methodName, Object... methodValue) {
        MethodAccess access = MethodAccess.get(obj.getClass());
        int index = access.getIndex(methodName);
        access.invoke(obj, index, methodValue);
    }

    //调用有参的方法，并返回值
    public Object invokeReturnValue(Object obj, String methodName, Object... methodValue) {
        MethodAccess access = MethodAccess.get(obj.getClass());
        int index = access.getIndex(methodName);
        return access.invoke(obj, index, methodValue);
    }

    //调用无参的方法
    public void invoke(Object obj, String methodName) {
        MethodAccess access = MethodAccess.get(obj.getClass());
        int index = access.getIndex(methodName);
        access.invoke(obj, index);
    }

    //调用无参的方法,并返回值
    public Object invokeReturnValue(Object obj, String methodName) {
        MethodAccess access = MethodAccess.get(obj.getClass());
        int index = access.getIndex(methodName);
        return access.invoke(obj, index);
    }

}
