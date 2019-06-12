package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

//其他薪金、计税加项和减项等子表的批量修改
@Data
@EqualsAndHashCode(of = "userName", callSuper = false)
public class ChildrenTableBatchModify extends BaseRowModel {
    @ExcelProperty(index = 0)
    private String userName;

    @ExcelProperty(index = 1)
    private String var1;

    @ExcelProperty(index = 2)
    private String var2;

    @ExcelProperty(index = 3)
    private String var3;

    @ExcelProperty(index = 4)
    private String var4;

    @ExcelProperty(index = 5)
    private String var5;

    @ExcelProperty(index = 6)
    private String var6;

    @ExcelProperty(index = 7)
    private String var7;

    @ExcelProperty(index = 8)
    private String var8;

    @ExcelProperty(index = 9)
    private String var9;

    @ExcelProperty(index = 10)
    private String var10;

    @ExcelProperty(index = 11)
    private String var11;

    @ExcelProperty(index = 12)
    private String var12;

    @ExcelProperty(index = 13)
    private String var13;

    @ExcelProperty(index = 14)
    private String var14;

    @ExcelProperty(index = 15)
    private String var15;

    @ExcelProperty(index = 16)
    private String var16;

    @ExcelProperty(index = 17)
    private String var17;

    @ExcelProperty(index = 18)
    private String var18;

    @ExcelProperty(index = 19)
    private String var19;

    @ExcelProperty(index = 20)
    private String var20;

    @ExcelProperty(index = 21)
    private String var21;

    @ExcelProperty(index = 22)
    private String var22;

    @ExcelProperty(index = 23)
    private String var23;

    @ExcelProperty(index = 24)
    private String var24;

    @ExcelProperty(index = 25)
    private String var25;

    @ExcelProperty(index = 26)
    private String var26;

    @ExcelProperty(index = 27)
    private String var27;

    @ExcelProperty(index = 28)
    private String var28;

    @ExcelProperty(index = 29)
    private String var29;

    @ExcelProperty(index = 20)
    private String var30;

}
