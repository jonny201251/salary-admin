package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeNameComment;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeUserName;
import com.hthyaq.salaryadmin.entity.ChangeSheet;
import com.hthyaq.salaryadmin.vo.ChangeSheetPageData;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * <p>
 * 变动单 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
public interface ChangeSheetService extends IService<ChangeSheet> {
    boolean saveOrUpdateComplexData(ChangeSheetPageData changeSheetPageData);

    ChangeSheetPageData editViewComplexData(Long changeSheetId);

    Task completeUserTask(String changeSheetId, String userName, String buttonName);

    int updateProcessStatusById(String changeSheetId, String taskName);

    void completeUserTaskAndSaveLeaderAnnotation(ChangeSheetPageData changeSheetPageData);

    List<OtherBonusIncludeNameComment> getOtherBonusIncludeNameComment(String tName);

    List<OtherBonusIncludeUserName> getOtherBonusIncludeUserName(String tName);
}
