package com.hthyaq.salaryadmin;

public class Test {
    public static void main(String[] args) {
        String fileName="a.b.txt";
        String tmp = fileName;

        fileName = tmp.substring(0, 3) ;
        System.out.println(fileName);
    }
}
