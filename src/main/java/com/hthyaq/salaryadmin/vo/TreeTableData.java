package com.hthyaq.salaryadmin.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * antd需要的tableTreeData数据结构
 * https://ant.design/components/table-cn/#components-table-demo-expand-children
 */
@Data
public class TreeTableData {
    private String name;
    private String pname;
    private String status;
    private String type;
    //id
    private Integer key;
    //id
    private Integer id;
    //ids
    private List<Integer> ids;
    private List<TreeTableData> children = Lists.newArrayList();
}
