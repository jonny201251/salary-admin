package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class MenuPage{
    private List<Menu> menus= Lists.newArrayList();
    private List<String> defaultOpenKeys= Lists.newArrayList();
}
