package com.hthyaq.salaryadmin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

//年份、月份的月结提示
@Data
@AllArgsConstructor
public class YearMonthFinishTip {
    private Integer year;
    private Integer month;
}
