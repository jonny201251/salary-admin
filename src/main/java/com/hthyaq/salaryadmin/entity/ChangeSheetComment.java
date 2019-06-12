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
 * 用于变动单走审批工作流时，保存领导的批注信息
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChangeSheetComment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 审批人
     */
    private String userName;

    /**
     * 审批人批注
     */
    private String comment;

    /**
     * 对应change_sheet表的id
     */
    private Long changeSheetId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
