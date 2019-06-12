package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.SalBonus;

import java.util.List;

/**
 * <p>
 * 工资-应发奖金或过节费表，用于内聘工资表和离退休工资表 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2019-01-02
 */
public interface SalBonusMapper extends BaseMapper<SalBonus> {
    List<SalBonus> getNpSalBonusByUserName(List<String> names);
    List<SalBonus> getLtxSalBonusByUserName(List<String> names);
}
