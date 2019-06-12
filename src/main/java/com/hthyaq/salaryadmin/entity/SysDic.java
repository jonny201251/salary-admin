package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 数据字典，包含职工类别、计税类别
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 注解
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 基础数据标识，如职工类别、计税类别
     */
    private String flag;

    /**
     * 名称
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


}
