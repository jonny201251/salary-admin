package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.entity.SysPermission;
import lombok.Data;

import java.util.List;

@Data
public class Menu extends SysPermission {
    private String key;
    private List<Menu> children= Lists.newArrayList();
}
