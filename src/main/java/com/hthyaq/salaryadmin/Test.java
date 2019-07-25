package com.hthyaq.salaryadmin;

import com.hthyaq.salaryadmin.entity.SalNp;

public class Test {
    public static void main(String[] args) {
        SalNp s=new SalNp();
        System.out.println("离退休之死亡".replaceAll("不在职之|离退休之",""));
    }
}
