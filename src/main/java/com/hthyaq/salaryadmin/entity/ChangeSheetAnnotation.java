package com.hthyaq.salaryadmin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 变动单审批时的领导批注
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChangeSheetAnnotation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程表中的taskId
     */
    private String taskId;

    /**
     * 领导批注
     */
    private String annotation;

    /**
     * 弹出框的操作按钮
     */
    private String buttonName;

    /**
     * 关联change_sheet表的id
     */
    private Long changeSheetId;


}
