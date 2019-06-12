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
 * 人员信息表
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 登录密码
     */
    private String pwd;

    /**
     * 职工编号
     */
    private String num;

    /**
     * 职工类别，具体查看sys_dic表
     */
    private String category;

    /**
     * 身份证号码
     */
    private String idNum;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 使用状态，正常和禁用
     */
    private String status;

    /**
     * 在职状态
     */
    private String job;

    /**
     * 工资发放方式，包括站发、院发
     */
    private String giveMode;

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
     * 关联部门的id,默认为0,即该人员没有设置部门
     */
    private Integer deptId;

    /**
     * 关联的部门名称
     */
    private String deptName;

    /**
     * dept_id的顶级节点，即单位名称
     */
    private String org;

    /**
     * 备注
     */
    private String comment;


}
