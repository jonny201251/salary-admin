package com.hthyaq.salaryadmin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class PageData<T> {
    //当前页码
    private Integer pageNum;
    //每页显示多少条记录
    private Integer pageSize;
    //总记录数
    private Integer totalCount;
    //总页数
    private Integer pageCount;
    //具体记录数
    private List<T> list;
}
