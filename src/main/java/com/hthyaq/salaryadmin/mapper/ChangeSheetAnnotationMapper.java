package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.ChangeSheetAnnotation;
import com.hthyaq.salaryadmin.vo.ChangeSheeTaskAndtAnnotation;

import java.util.List;

/**
 * <p>
 * 变动单审批时的领导批注 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-30
 */
public interface ChangeSheetAnnotationMapper extends BaseMapper<ChangeSheetAnnotation> {
    List<ChangeSheeTaskAndtAnnotation> getAnnotationByProcessInstanceId(String changeSheetId);
}
