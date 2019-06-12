package com.hthyaq.salaryadmin.bo.otherBonus;

import lombok.Data;

@Data
public class OtherBonusIncludeUserName {
    private String userDeptName;
    private String userName;
    //其他薪金的名称
    private String name;
    private Double money;
    private String comment;
}
