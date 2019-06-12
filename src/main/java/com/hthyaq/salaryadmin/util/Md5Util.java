package com.hthyaq.salaryadmin.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 *登录密码采用了加盐的MD5
 */
public class Md5Util {
    private static final String SALT = "com.hthyaq.salaryadmin";

    private static final ThreadLocal<HashFunction> td=ThreadLocal.withInitial(()->Hashing.md5());

    public static String encryPassword(String password){
        return td.get().hashString(password+SALT, Charset.forName("UTF-8")).toString();
    }

    public static void main(String[] args) {
        System.out.println(Md5Util.encryPassword("aaaaaaaa"));
        System.out.println(Md5Util.encryPassword("aaaaaaaa"));
        System.out.println(Md5Util.encryPassword("aaaaaaaa"));
    }
}
