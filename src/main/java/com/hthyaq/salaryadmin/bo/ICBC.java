package com.hthyaq.salaryadmin.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.experimental.Accessors;

//内聘、离退休的导出，用于工行模板
@Data
@Accessors(chain = true)
public class ICBC extends BaseRowModel {
    @ExcelProperty(value = {"姓名"})
    private String userName;
    //银行账号
    @ExcelProperty(value = {"卡号"})
    private String userBankAccount;
    @ExcelProperty(value = {"应处理金额"})
    private Double shouldMoney;
    @ExcelProperty(value = {"备注"})
    private String comment;
    @ExcelProperty(value = {"实处理金额"})
    private Double money;
    @ExcelProperty(value = {"处理标志"})
    private String handleFlag;
}
