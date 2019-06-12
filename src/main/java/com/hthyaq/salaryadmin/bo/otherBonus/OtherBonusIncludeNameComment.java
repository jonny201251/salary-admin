package com.hthyaq.salaryadmin.bo.otherBonus;

import lombok.Data;

//用于查询出 内聘+退休+离休-其他薪金的名称和备注
@Data
public class OtherBonusIncludeNameComment {
    private String name;
    private String comment;
}
