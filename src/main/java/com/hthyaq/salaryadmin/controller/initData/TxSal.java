package com.hthyaq.salaryadmin.controller.initData;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of="name", callSuper = false)
public class TxSal extends BaseRowModel {
    @ExcelProperty(value = {"姓名"})
    private String name;
    /**
     * 基本退休费
     */
    @ExcelProperty(value = {"基本退休费"})
    private Double jiben;

    /**
     * 规范补贴
     */
    @ExcelProperty(value = {"规范补贴"})
    private Double guifan;

    /**
     * 保留补贴
     */
    @ExcelProperty(value = {"保留补贴"})
    private Double baoliu;

    /**
     * 171补贴
     */
    @ExcelProperty(value = {"171补贴"})
    private Double butie;

    /**
     * 书报洗理费
     */
    @ExcelProperty(value = {"书报洗理费"})
    private Double shubao;

    /**
     * 提租
     */
    @ExcelProperty(value = {"提租"})
    private Double tizu;

    /**
     * 34-39调
     */
    @ExcelProperty(value = {"34-39调"})
    private Double tiao;

    /**
     * 站内补差
     */
    @ExcelProperty(value = {"站内补差"})
    private Double bucha;

    /**
     * 1617增资
     */
    @ExcelProperty(value = {"1617增资"})
    private Double zengzi;

    /**
     * 应发其他
     */
    @ExcelProperty(value = {"应发其他"})
    private Double yingfaqita;

    /**
     * 房租
     */
    @ExcelProperty(value = {"房租"})
    private Double fangzu;

    /**
     * 应扣其他
     */
    @ExcelProperty(value = {"应扣其他"})
    private Double yingkouqita;

}
