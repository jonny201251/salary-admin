package com.hthyaq.salaryadmin.vo;


import com.hthyaq.salaryadmin.entity.SysUser;
import lombok.Data;
import lombok.experimental.Accessors;

//将用户信息转到工资中的用户信息
@Data
@Accessors(chain = true)
public class SalPage {
    private String userName;
    private String userGiveMode;
    private String userCategory;
    private Integer userDeptId;
    private String userDeptName;
    private String yearmonthString;

    public static SalPage converter(SysUser sysUser, String yearmonthString) {
        SalPage salPage = new SalPage();
        salPage.setUserName(sysUser.getName())
                .setUserGiveMode(sysUser.getGiveMode())
                .setUserCategory(sysUser.getCategory())
                .setUserDeptId(sysUser.getDeptId())
                .setUserDeptName(sysUser.getDeptName())
                .setYearmonthString(yearmonthString);
        return salPage;
    }
}