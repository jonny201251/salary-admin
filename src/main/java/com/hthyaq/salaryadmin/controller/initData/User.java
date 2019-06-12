package com.hthyaq.salaryadmin.controller.initData;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="name", callSuper = false)
public class User extends BaseRowModel {
    @ExcelProperty(value = {"部门"})
    private String deptName;
    @ExcelProperty(value = {"员工编号"})
    private String num;
    @ExcelProperty(value = {"姓名"})
    private String name;
    @ExcelProperty(value = {"身份证号"})
    private String idNum;
    @ExcelProperty(value = {"手机号"})
    private String mobile;
}
