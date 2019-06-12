package com.hthyaq.salaryadmin.bo;

import lombok.Data;

//用于导出报税数据
@Data
public class UpTax {
    //年份
    private Integer year;
    //月份
    private Integer month;
    //姓名
    private String name;
    //证照号码
    private String idNum;
    //本期收入=应发合计+其他薪金+计税加项
    private Double income;
    //本期免税收入=其他薪金（应发不计税）
    private Double noTax;
    //基本养老保险费
    private Double yanglao;
    //基本医疗保险费
    private Double yiliao;
    //失业保险费
    private Double shiye;
    //住房公积金
    private Double zhufang;
    //职业年金
    private Double nianjin;
}
