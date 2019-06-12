package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 工资内聘-计税专用项
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SalNpTax implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 专用项名称
     */
    private String name;

    /**
     * 金额
     */
    private Double money;

    /**
     * 计税专用项：加项、减项
     */
    private String type;

    /**
     * sal_np表的id
     */
    private Long salNpId;


}
