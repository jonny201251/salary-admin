package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hthyaq.salaryadmin.entity.ChangeSheetAnnotation;
import com.hthyaq.salaryadmin.mapper.ChangeSheetAnnotationMapper;
import com.hthyaq.salaryadmin.service.ChangeSheetAnnotationService;
import com.hthyaq.salaryadmin.service.WorkFlowService;
import com.hthyaq.salaryadmin.vo.ChangeSheeTaskAndtAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 变动单审批时的领导批注 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-30
 */
@Service
public class ChangeSheetAnnotationServiceImpl extends ServiceImpl<ChangeSheetAnnotationMapper, ChangeSheetAnnotation> implements ChangeSheetAnnotationService {
    @Autowired
    WorkFlowService workFlowService;
    @Override
    public List<ChangeSheeTaskAndtAnnotation> getAnnotationByChangeSheetId(String changeSheetId) {
        String processInstanceId=workFlowService.getProcessInstanceIdByBusinessId(changeSheetId);
        List<ChangeSheeTaskAndtAnnotation> annotationList = this.baseMapper.getAnnotationByProcessInstanceId(processInstanceId);
        if(!workFlowService.isFinishByBusinessId(changeSheetId)){
            //删除最后一个
            annotationList.remove(annotationList.size()-1);
        }
        return annotationList;
    }
}
