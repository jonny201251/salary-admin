package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

//离休工资的应发和应扣款项的批量修改
@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class YingfaYingkouSalLx extends BaseRowModel {
    @ExcelProperty(value = {"姓名"}, index = 0)
    private String name;

    @ExcelProperty(value = {"基本离休费"}, index = 1)
    private Double jiben;

    @ExcelProperty(value = {"离休人员补贴"}, index = 2)
    private Double butie1;

    @ExcelProperty(value = {"护理费"}, index = 3)
    private Double huli;

    @ExcelProperty(value = {"电话费"}, index = 4)
    private Double dianhua;

    @ExcelProperty(value = {"交通费"}, index = 5)
    private Double jiaotong;

    @ExcelProperty(value = {"171补贴"}, index = 6)
    private Double butie2;

    @ExcelProperty(value = {"书报洗理"}, index = 7)
    private Double shubao;

    @ExcelProperty(value = {"提租补贴"}, index = 8)
    private Double butie3;

    @ExcelProperty(value = {"14年调资"}, index = 9)
    private Double tiaozi1;

    @ExcelProperty(value = {"34-39年调资"}, index = 10)
    private Double tiaozi2;

    @ExcelProperty(value = {"站内补差"}, index = 11)
    private Double bucha;

    @ExcelProperty(value = {"1612增资"}, index = 12)
    private Double zengzi;

    @ExcelProperty(value = {"物业补贴"}, index = 13)
    private Double wuye;

    @ExcelProperty(value = {"应发其他"}, index = 14)
    private Double yingfaqita;

    @ExcelProperty(value = {"房租"}, index = 15)
    private Double fangzu;

    @ExcelProperty(value = {"应扣其他"}, index = 16)
    private Double yingkouqita;
}
