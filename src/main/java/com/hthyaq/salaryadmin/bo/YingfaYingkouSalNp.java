package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

//内聘工资的应发和应扣款项的批量修改
@Data
@EqualsAndHashCode(of="name", callSuper = false)
public class YingfaYingkouSalNp extends BaseRowModel {
    @ExcelProperty(value = {"姓名"})
    private String name;

    @ExcelProperty(value = {"岗位工资"})
    private Double gangwei;

    @ExcelProperty(value = {"薪级工资"})
    private Double xinji;

    @ExcelProperty(value = {"补差"})
    private Double bucha;

    @ExcelProperty(value = {"部内"})
    private Double bunei;

    @ExcelProperty(value = {"浮动"})
    private Double fudong;

    @ExcelProperty(value = {"航龄"})
    private Double hangling;

    @ExcelProperty(value = {"工补"})
    private Double gongbu;

    @ExcelProperty(value = {"书洗"})
    private Double shuxi;

    @ExcelProperty(value = {"菜磨"})
    private Double caimo;

    @ExcelProperty(value = {"职补1-9"})
    private Double zhibu;

    @ExcelProperty(value = {"10%保留"})
    private Double baoliu;

    @ExcelProperty(value = {"季度效益"})
    private Double jidu;

    @ExcelProperty(value = {"水电燃补"})
    private Double shuidian;

    @ExcelProperty(value = {"效益补贴"})
    private Double xiaoyi;

    @ExcelProperty(value = {"岗津"})
    private Double gangjin;

    @ExcelProperty(value = {"单身补"})
    private Double danshengbu;

    @ExcelProperty(value = {"其他"})
    private Double qita;

    @ExcelProperty(value = {"房租"})
    private Double fangzu;

    @ExcelProperty(value = {"养老保险"})
    private Double yanglao;

    @ExcelProperty(value = {"职业年金"})
    private Double nianjin;

    @ExcelProperty(value = {"住房"})
    private Double zhufang;

    @ExcelProperty(value = {"失业保险"})
    private Double shiye;

    @ExcelProperty(value = {"扣款"})
    private Double koukuan;

    @ExcelProperty(value = {"医疗保险"})
    private Double yiliao;
}
