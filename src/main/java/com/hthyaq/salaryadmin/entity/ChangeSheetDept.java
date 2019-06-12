package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 变动单-部门，来源于内聘
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChangeSheetDept implements Serializable {

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
     * 操作类型，名称变更、部门重组
     */
    private String type;

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
     * 根据该字段进行排序显示
     */
    private Double sort;

    /**
     * 第一次被修改的部门名称
     */
    private String startDept;

    /**
     * 对应sys_dept表的id
     */
    private Integer sysDeptId;

    /**
     * 外键，对应change_sheet中的id
     */
    private Long changeSheetId;


}
