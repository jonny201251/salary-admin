package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 工资内聘
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)--这个注释会引起反射时找不到set方法，所以去掉了
public class SalNp implements Serializable {

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
     * 岗位工资
     */
    private Double gangwei;

    /**
     * 薪级工资
     */
    private Double xinji;

    /**
     * 补差
     */
    private Double bucha;

    /**
     * 部内
     */
    private Double bunei;

    /**
     * 浮动
     */
    private Double fudong;

    /**
     * 航龄
     */
    private Double hangling;

    /**
     * 工补
     */
    private Double gongbu;

    /**
     * 书洗
     */
    private Double shuxi;

    /**
     * 菜磨
     */
    private Double caimo;

    /**
     * 职补1-9
     */
    private Double zhibu;

    /**
     * 10%保留
     */
    private Double baoliu;

    /**
     * 季度效益
     */
    private Double jidu;

    /**
     * 水电燃补
     */
    private Double shuidian;

    /**
     * 效益补贴
     */
    private Double xiaoyi;

    /**
     * 岗津
     */
    private Double gangjin;

    /**
     * 单身补
     */
    private Double danshengbu;

    /**
     * 物业补贴
     */
    private Double wuye;

    /**
     * 其他
     */
    private Double qita;

    /**
     * 房租
     */
    private Double fangzu;

    /**
     * 养老保险
     */
    private Double yanglao;

    /**
     * 职业年金
     */
    private Double nianjin;

    /**
     * 住房
     */
    private Double zhufang;

    /**
     * 税款1
     */
    private Double shuikuan1;

    /**
     * 税款2
     */
    private Double shuikuan2;

    /**
     * 失业保险
     */
    private Double shiye;

    /**
     * 扣款
     */
    private Double koukuan;

    /**
     * 医疗保险
     */
    private Double yiliao;

    /**
     * 应发合计
     */
    private Double yingfa;

    /**
     * 应扣合计
     */
    private Double yingkou;

    /**
     * 应发奖金和过节费合计，即其他薪金
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
     * sys_user表的id_num
     */
    private String userIdNum;

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

    /**
     * 本年真正的月份，用于计算计税减项中的基本扣除项
     */
    private Integer realMonth;

    /**
     * 年月的字符串类型，用于接受前端日期数据
     */
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
