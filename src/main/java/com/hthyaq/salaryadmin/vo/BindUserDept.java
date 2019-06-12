package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class BindUserDept {
    private Integer deptId;
    private String deptName;
    private List<Integer> checkUserValues= Lists.newArrayList();
    private List<LabelValueInteger> users= Lists.newArrayList();
}
