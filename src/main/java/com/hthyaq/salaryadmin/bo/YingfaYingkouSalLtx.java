package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

//离退休工资的应发和应扣款项的批量修改
@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class YingfaYingkouSalLtx extends BaseRowModel {
    @ExcelProperty(value = {"姓名"}, index = 0)
    private String name;

    @ExcelProperty(value = {"基本退休费"}, index = 1)
    private Double jiben;

    @ExcelProperty(value = {"规范补贴"}, index = 2)
    private Double guifan;

    @ExcelProperty(value = {"保留补贴"}, index = 3)
    private Double baoliu;

    @ExcelProperty(value = {"171补贴"}, index = 4)
    private Double butie;

    @ExcelProperty(value = {"书报洗理费"}, index = 5)
    private Double shubao;

    @ExcelProperty(value = {"提租"}, index = 6)
    private Double tizu;

    @ExcelProperty(value = {"34-39调"}, index = 7)
    private Double tiao;

    @ExcelProperty(value = {"站内补差"}, index = 8)
    private Double bucha;

    @ExcelProperty(value = {"1617增资"}, index = 9)
    private Double zengzi;

    @ExcelProperty(value = {"应发其他"}, index = 10)
    private Double yingfaqita;

    @ExcelProperty(value = {"物业补贴"}, index = 11)
    private Double wuye;

    @ExcelProperty(value = {"房租"}, index = 12)
    private Double fangzu;

    @ExcelProperty(value = {"应扣其他"}, index = 13)
    private Double yingkouqita;

}
