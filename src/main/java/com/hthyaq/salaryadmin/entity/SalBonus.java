package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 工资-应发奖金或过节费表，用于内聘工资表和离退休工资表
 * </p>
 *
 * @author zhangqiang
 * @since 2019-01-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SalBonus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 奖金或过节费的名称
     */
    private String name;

    /**
     * 金额
     */
    private Double money;

    /**
     * 计税类别,具体查看sys_dic
     */
    private String type;

    /**
     * 备注
     */
    private String comment;

    /**
     * 值为sal_np、sal_ltx、sal_lx
     */
    private String tName;

    /**
     * 关联sal_np、sal_ltx、sal_lx表的id
     */
    private Long salId;


}
