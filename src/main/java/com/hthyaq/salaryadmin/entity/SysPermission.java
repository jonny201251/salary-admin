package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限表，别名资源表、菜单表
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 上级节点的id
     */
    private Integer pid;

    /**
     * 上级权限的名称
     */
    private String pname;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限的url
     */
    private String url;

    /**
     * 菜单的图标
     */
    private String icon;

    /**
     * 权限类型，包括导航菜单、操作按钮
     */
    private String type;

    /**
     * 等级，方便通过等级字段查询出权限
     */
    private Integer level;

    /**
     * 排序
     */
    private Double sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
