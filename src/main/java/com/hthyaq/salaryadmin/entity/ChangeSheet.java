package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 变动单
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChangeSheet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 变动单名称，格式为2018年12月变动单
     */
    private String name;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 年月的字符串类型，用于接受前端日期数据
     */
    private String yearmonthString;

    /**
     * 年份和月份的组合，用于查询统计
     */
    private Integer yearmonthInt;

    /**
     * 备注
     */
    private String comment;

    /**
     * 流程状态，审批节点名称、已结束
     */
    private String processStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
