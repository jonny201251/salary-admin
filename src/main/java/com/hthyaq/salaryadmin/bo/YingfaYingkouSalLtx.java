package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

//离退休工资的应发和应扣款项的批量修改
@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class YingfaYingkouSalLtx extends BaseRowModel {
    @ExcelProperty(value = {"姓名"})
    private String name;

    @ExcelProperty(value = {"基本退休费"})
    private Double jiben;

    @ExcelProperty(value = {"规范补贴"})
    private Double guifan;

    @ExcelProperty(value = {"保留补贴"})
    private Double baoliu;

    @ExcelProperty(value = {"171补贴"})
    private Double butie;

    @ExcelProperty(value = {"书报洗理费"})
    private Double shubao;

    @ExcelProperty(value = {"提租"})
    private Double tizu;

    @ExcelProperty(value = {"34-39调"})
    private Double tiao;

    @ExcelProperty(value = {"站内补差"})
    private Double bucha;

    @ExcelProperty(value = {"1617增资"})
    private Double zengzi;

    @ExcelProperty(value = {"应发其他"})
    private Double yingfaqita;

    @ExcelProperty(value = {"房租"})
    private Double fangzu;

    @ExcelProperty(value = {"应扣其他"})
    private Double yingkouqita;

}
