package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

//内聘工资的应发和应扣款项的批量修改
@Data
@EqualsAndHashCode(of = "name", callSuper = false)
public class YingfaYingkouSalNp extends BaseRowModel {
    @ExcelProperty(value = {"姓名"}, index = 0)
    private String name;

    @ExcelProperty(value = {"岗位工资"}, index = 1)
    private Double gangwei;

    @ExcelProperty(value = {"薪级工资"}, index = 2)
    private Double xinji;

    @ExcelProperty(value = {"补差"}, index = 3)
    private Double bucha;

    @ExcelProperty(value = {"部内"}, index = 4)
    private Double bunei;

    @ExcelProperty(value = {"浮动"}, index = 5)
    private Double fudong;

    @ExcelProperty(value = {"航龄"}, index = 6)
    private Double hangling;

    @ExcelProperty(value = {"工补"}, index = 7)
    private Double gongbu;

    @ExcelProperty(value = {"书洗"}, index = 8)
    private Double shuxi;

    @ExcelProperty(value = {"菜磨"}, index = 9)
    private Double caimo;

    @ExcelProperty(value = {"职补1-9"}, index = 10)
    private Double zhibu;

    @ExcelProperty(value = {"10%保留"}, index = 11)
    private Double baoliu;

    @ExcelProperty(value = {"季度效益"}, index = 12)
    private Double jidu;

    @ExcelProperty(value = {"水电燃补"}, index = 13)
    private Double shuidian;

    @ExcelProperty(value = {"效益补贴"}, index = 14)
    private Double xiaoyi;

    @ExcelProperty(value = {"岗津"}, index = 15)
    private Double gangjin;

    @ExcelProperty(value = {"单身补"}, index = 16)
    private Double danshengbu;

    @ExcelProperty(value = {"其他"}, index = 17)
    private Double qita;

    @ExcelProperty(value = {"房租"}, index = 18)
    private Double fangzu;

    @ExcelProperty(value = {"养老保险"}, index = 19)
    private Double yanglao;

    @ExcelProperty(value = {"职业年金"}, index = 20)
    private Double nianjin;

    @ExcelProperty(value = {"住房"}, index = 21)
    private Double zhufang;

    @ExcelProperty(value = {"失业保险"}, index = 22)
    private Double shiye;

    @ExcelProperty(value = {"扣款"}, index = 23)
    private Double koukuan;

    @ExcelProperty(value = {"医疗保险"}, index = 24)
    private Double yiliao;
}
