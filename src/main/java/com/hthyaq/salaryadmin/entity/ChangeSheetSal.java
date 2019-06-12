package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 变动单-工资，来源于内聘和退休
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Data
@Accessors(chain = true)
public class ChangeSheetSal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 职工编号,sal_np、sal_ltx表的user_num
     */
    private String num;

    /**
     * 部门,sal_np、sal_ltx表的user_dept_name
     */
    private String deptName;

    /**
     * 职工姓名,sal_np、sal_ltx表的user_name
     */
    private String name;

    /**
     * 职工类别,sal_np、sal_ltx表的user_category
     */
    private String category;

    /**
     * 职工类别,sal_np、sal_ltx表的user_job
     */
    private String job;

    /**
     * 变动项名称
     */
    private String changeName;

    /**
     * 上月变动项的金额
     */
    private Double lastMoney;

    /**
     * 当月的变动项金额
     */
    private Double currentMoney;

    /**
     * 对应sal_np、sal_ltx的comment
     */
    private String reason;

    /**
     * sys_user中的sort
     */
    private Double userSort;

    /**
     * 值为sal_np、sal_ltx、sal_lx
     */
    private String tName;

    /**
     * 关联sal_np、sal_ltx表的id
     */
    private Long salId;

    /**
     * 对应change_sheet表的id
     */
    private Long changeSheetId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeSheetSal that = (ChangeSheetSal) o;
        return Objects.equals(year, that.year) &&
                Objects.equals(month, that.month) &&
                Objects.equals(num, that.num) &&
                Objects.equals(changeName, that.changeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, num, changeName);
    }
}
