package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysDept implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 上级节点的id，有效值从1开始，其中1表示单位名称
     */
    private Integer pid;

    /**
     * 上级节点的部门名称
     */
    private String pname;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 使用状态，正常和禁用
     */
    private String status;

    /**
     * 根据该字段进行排序显示
     */
    private Double sort;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String comment;


}
