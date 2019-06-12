package com.hthyaq.salaryadmin.util;

public class IDNumUtil {
    public static void main(String[] args) {
        System.out.println(idNum("123456", 3));
    }

    //获取身份证号后几位
    public static String idNum(String id, Integer count) {
        count -= 1;
        StringBuilder sb = new StringBuilder();
        String[] ids = id.split("");
        for (int i = ids.length - 1; i >= ids.length - 1 - count; i--) {
            sb.append(ids[i]);
        }
        return sb.reverse().toString();
    }
}
