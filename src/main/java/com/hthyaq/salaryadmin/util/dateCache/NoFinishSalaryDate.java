package com.hthyaq.salaryadmin.util.dateCache;

import lombok.AllArgsConstructor;
import lombok.Data;

//内聘、退休、离休工资的未月结的日期
@Data
@AllArgsConstructor
public class NoFinishSalaryDate {
    private Integer year;

    private Integer month;

    private String yearmonthString;

    private Integer yearmonthInt;
}
