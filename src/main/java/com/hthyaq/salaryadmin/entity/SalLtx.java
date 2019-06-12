package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 工资离退休
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)--这个注释会引起反射时找不到set方法，所以去掉了
public class SalLtx implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 做月结时，上个月该记录的id
     */
    private Long lastId;

    /**
     * 基本退休费
     */
    private Double jiben;

    /**
     * 规范补贴
     */
    private Double guifan;

    /**
     * 保留补贴
     */
    private Double baoliu;

    /**
     * 171补贴
     */
    private Double butie;

    /**
     * 书报洗理费
     */
    private Double shubao;

    /**
     * 提租
     */
    private Double tizu;

    /**
     * 34-39调
     */
    private Double tiao;

    /**
     * 站内补差
     */
    private Double bucha;

    /**
     * 1617增资
     */
    private Double zengzi;

    /**
     * 应发其他
     */
    private Double yingfaqita;

    /**
     * 房租
     */
    private Double fangzu;

    /**
     * 应扣其他
     */
    private Double yingkouqita;

    /**
     * 应发合计
     */
    private Double yingfa;

    /**
     * 应扣合计
     */
    private Double yingkou;

    /**
     * 应发奖金和过节费合计
     */
    private Double jiangjin;

    /**
     * 实发工资
     */
    private Double shifa;

    /**
     * sys_user表的id
     */
    private Integer userId;

    /**
     * sys_user表的name
     */
    private String userName;

    /**
     * sys_user表的num
     */
    private String userNum;

    /**
     * sys_user表的category
     */
    private String userCategory;

    /**
     * sys_user表的bank_account
     */
    private String userBankAccount;

    /**
     * sys_user表的status
     */
    private String userStatus;

    /**
     * sys_user表的job
     */
    private String userJob;

    /**
     * sys_user表的give_mode
     */
    private String userGiveMode;

    /**
     * sys_user表的sort
     */
    private Double userSort;

    private Integer userDeptId;

    /**
     * sys_user表的dept_name
     */
    private String userDeptName;

    /**
     * sys_user表的org
     */
    private String userOrg;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    private String yearmonthString;

    /**
     * 年份和月份的组合，用于查询统计
     */
    private Integer yearmonthInt;

    /**
     * 月结：未月结，已月结
     */
    private String finish;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String comment;


}
