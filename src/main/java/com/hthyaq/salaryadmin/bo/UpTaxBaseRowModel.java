package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.experimental.Accessors;

//导出报税数据
@Data
@Accessors(chain = true)
public class UpTaxBaseRowModel extends BaseRowModel {
    @ExcelProperty(value = {"工号"})
    private String jobNum;
    @ExcelProperty(value = {"姓名"})
    private String name;
    @ExcelProperty(value = {"*证照类型"})
    private String type;
    @ExcelProperty(value = {"*证照号码"})
    private String idNum;
    @ExcelProperty(value = {"*本期收入"})
    private Double income;
    @ExcelProperty(value = {"本期免税收入"})
    private Double noTax;
    @ExcelProperty(value = {"基本养老保险费"})
    private Double yanglao;
    @ExcelProperty(value = {"基本医疗保险费"})
    private Double yiliao;
    @ExcelProperty(value = {"失业保险费"})
    private Double shiye;
    @ExcelProperty(value = {"住房公积金"})
    private Double zhufang;

    @ExcelProperty(value = {"累计子女教育"})
    private Double var1;
    @ExcelProperty(value = {"累计赡养老人"})
    private Double var5;
    @ExcelProperty(value = {"累计继续教育"})
    private Double var2;
    @ExcelProperty(value = {"累计住房贷款利息"})
    private Double var3;
    @ExcelProperty(value = {"累计住房租金"})
    private Double var4;
    @ExcelProperty(value = {"企业(职业)年金"})
    private Double var6;
    @ExcelProperty(value = {"商业健康保险"})
    private Double var7;
    @ExcelProperty(value = {"税延养老保险"})
    private String var8;
    @ExcelProperty(value = {"其他"})
    private String var9;
    @ExcelProperty(value = {"准予扣除的捐赠额"})
    private String var10;
    @ExcelProperty(value = {"减免税额"})
    private String var11;
    @ExcelProperty(value = {"备注"})
    private String var12;

}
