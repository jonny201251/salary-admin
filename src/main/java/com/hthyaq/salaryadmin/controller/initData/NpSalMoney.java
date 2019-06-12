package com.hthyaq.salaryadmin.controller.initData;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="name", callSuper = false)
public class NpSalMoney extends BaseRowModel {
    @ExcelProperty(value = {"姓名"})
    private String name;
    @ExcelProperty(value = {"金额"})
    private Double money;
}
