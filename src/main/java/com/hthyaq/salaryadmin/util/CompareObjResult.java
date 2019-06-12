package com.hthyaq.salaryadmin.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CompareObjResult {
    //属性名
    private String name;
    //旧值
    private Object oldValue;
    //新值
    private Object newValue;
}
