package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 变动单-用户，来源于内聘
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChangeSheetUser implements Serializable {

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
     * 操作类型，新增人员、人员部门调动、不在职
     */
    private String type;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职工编号
     */
    private String num;

    /**
     * 旧部门名称
     */
    private String oldDept;

    /**
     * 新部门名称
     */
    private String newDept;

    /**
     * 变动原因，对应sys_user中的comment
     */
    private String reason;

    /**
     * sys_user中的sort
     */
    private Double userSort;

    /**
     * 第一次被修改的部门名称
     */
    private String startDept;

    /**
     * 对应sys_user表的id
     */
    private Integer sysUserId;

    /**
     * 外键，对应change_sheet中的id
     */
    private Long changeSheetId;


}
