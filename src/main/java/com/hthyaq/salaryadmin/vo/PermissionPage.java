package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data @Accessors(chain = true)
public class PermissionPage {
    private Integer id;
    //权限名称
    private String name;
    //上级权限名称
    private String pname;
    private List<Integer> checkValues= Lists.newArrayList();
    private List<LabelValueInteger> permissionList= Lists.newArrayList();
}
