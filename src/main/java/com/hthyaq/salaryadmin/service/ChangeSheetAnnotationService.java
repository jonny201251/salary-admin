package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.ChangeSheetAnnotation;
import com.hthyaq.salaryadmin.vo.ChangeSheeTaskAndtAnnotation;

import java.util.List;

/**
 * <p>
 * 变动单审批时的领导批注 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-30
 */
public interface ChangeSheetAnnotationService extends IService<ChangeSheetAnnotation> {
    List<ChangeSheeTaskAndtAnnotation> getAnnotationByChangeSheetId(String changeSheetId);
}
