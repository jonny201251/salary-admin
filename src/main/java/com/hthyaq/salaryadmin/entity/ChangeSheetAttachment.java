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
 * 
 * </p>
 *
 * @author zhangqiang
 * @since 2019-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false,of={""})
@Accessors(chain = true)
public class ChangeSheetAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 上传人
     */
    private String userName;

    /**
     * 上传的文件名
     */
    private String fileName;


    /**
     * 文件的物理路径
     */
    private String path;

    /**
     * 备注
     */
    private String comment;

    /**
     * 排序
     */
    private Double sort;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;

    /**
     * 值为sal_np、sal_ltx
     */
    private String tName;

    /**
     * 对应change_sheet表的id
     */
    private Long changeSheetId;


}
