package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.TreeSet;

@Data
public class BindUserPage {
    private List<TreeSelectData> treeSelectData;
    private List<String> expandedKeys= Lists.newArrayList();
    private TreeSet<String> checkedKeys= Sets.newTreeSet();
    private List<BindUserDept> userDeptList=Lists.newArrayList();
}
