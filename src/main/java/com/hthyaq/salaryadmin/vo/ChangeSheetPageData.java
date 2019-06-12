package com.hthyaq.salaryadmin.vo;

import com.hthyaq.salaryadmin.entity.*;
import lombok.Data;
import lombok.experimental.Accessors;

//变动单的页面数据
@Data
@Accessors(chain = true)
public class ChangeSheetPageData extends ChangeSheet {
    private Repeater<ChangeSheetUser> user_repeater;
    private Repeater<ChangeSheetDept> dept_repeater;
    private Repeater<ChangeSheetSal> salNp_repeater;
    private Repeater<ChangeSheetSal> salTx_repeater;
    private Repeater<ChangeSheetBonus> jiangjin_repeater;
    //Dialog的操作按钮
    private String buttonName;
    //待处理的变动单的菜单的操作按钮
    private String type;
    //userName
    private String userName;
    //领导批注
    private String annotation;
}
