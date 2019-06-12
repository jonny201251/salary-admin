package com.hthyaq.salaryadmin.bo.otherBonus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtherBonusHeader {
    //薪金的名称
    private String name;
    //记录OtherBonusModel中的var的后面的数字
    private Integer index;
    //备注
    private String comment;
}
