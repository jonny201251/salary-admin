package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 变动单-奖金或过节费，来源于内聘和退休
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChangeSheetBonus implements Serializable {

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
     * 过节费、奖金,sal_bonus表的name
     */
    private String name;

    /**
     * 总金额
     */
    private Double money;

    /**
     * 备注
     */
    private String comment;

    /**
     * 排序
     */
    private Double sort;

    /**
     * 对应change_sheet表的id
     */
    private Long changeSheetId;


}
