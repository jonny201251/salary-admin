package com.hthyaq.salaryadmin.service.impl;

import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.mapper.SalBonusMapper;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工资-应发奖金或过节费表，用于内聘工资表和离退休工资表 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2019-01-02
 */
@Service
public class SalBonusServiceImpl extends ServiceImpl<SalBonusMapper, SalBonus> implements SalBonusService {

    @Override
    public List<SalBonus> getNpSalBonusByUserName(List<String> names) {
        return this.baseMapper.getNpSalBonusByUserName(names);
    }

    @Override
    public List<SalBonus> getLtxSalBonusByUserName(List<String> names) {
        return this.baseMapper.getLtxSalBonusByUserName(names);
    }
}
