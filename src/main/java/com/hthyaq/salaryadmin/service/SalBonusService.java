package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SalBonus;

import java.util.List;

/**
 * <p>
 * 工资-应发奖金或过节费表，用于内聘工资表和离退休工资表 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2019-01-02
 */
public interface SalBonusService extends IService<SalBonus> {
    List<SalBonus> getNpSalBonusByUserName(List<String> names);
    List<SalBonus> getLtxSalBonusByUserName(List<String> names);
}
