package com.hthyaq.salaryadmin.service.impl;

import com.hthyaq.salaryadmin.entity.ChangeSheetComment;
import com.hthyaq.salaryadmin.mapper.ChangeSheetCommentMapper;
import com.hthyaq.salaryadmin.service.ChangeSheetCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用于变动单走审批工作流时，保存领导的批注信息 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-24
 */
@Service
public class ChangeSheetCommentServiceImpl extends ServiceImpl<ChangeSheetCommentMapper, ChangeSheetComment> implements ChangeSheetCommentService {

}
