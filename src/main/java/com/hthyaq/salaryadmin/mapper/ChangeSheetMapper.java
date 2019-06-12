package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeNameComment;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeUserName;
import com.hthyaq.salaryadmin.entity.ChangeSheet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 变动单 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
public interface ChangeSheetMapper extends BaseMapper<ChangeSheet> {
    int updateProcessStatusById(@Param("changeSheetId") Integer changeSheetId, @Param("taskName") String taskName);

    List<OtherBonusIncludeNameComment> getOtherBonusIncludeNameCommentBySalNp(@Param("year") Integer year, @Param("month") Integer month);

    List<OtherBonusIncludeNameComment> getOtherBonusIncludeNameCommentBySalLtx(@Param("year") Integer year, @Param("month") Integer month);

    List<OtherBonusIncludeNameComment> getOtherBonusIncludeNameCommentBySalLx(@Param("year") Integer year, @Param("month") Integer month);

    List<OtherBonusIncludeUserName> getOtherBonusIncludeUserNameBySalNp(@Param("year") Integer year, @Param("month") Integer month);

    List<OtherBonusIncludeUserName> getOtherBonusIncludeUserNameBySalLtx(@Param("year") Integer year, @Param("month") Integer month);

    List<OtherBonusIncludeUserName> getOtherBonusIncludeUserNameBySalLx(@Param("year") Integer year, @Param("month") Integer month);
}
