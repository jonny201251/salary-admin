package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * antd需要的treedata数据结构
 * https://ant.design/components/tree-select-cn/
 */
@Data
public class TreeSelectData {
    //名称
    private String title;
    //id
    private Integer value;
    //id
    private Integer key;
    private List<TreeSelectData> children = Lists.newArrayList();
}
