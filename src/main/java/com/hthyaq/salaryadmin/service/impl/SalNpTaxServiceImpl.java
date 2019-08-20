package com.hthyaq.salaryadmin.service.impl;

import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.mapper.SalNpTaxMapper;
import com.hthyaq.salaryadmin.service.SalNpTaxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 计税专用项-工资内聘 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Service
public class SalNpTaxServiceImpl extends ServiceImpl<SalNpTaxMapper, SalNpTax> implements SalNpTaxService {

    @Override
    public List<SalNpTax> getSalNpTaxByUserName(List<String> names) {
        return this.baseMapper.getSalNpTaxByUserName(names);
    }

    @Override
    public List<SalNpTax> getSalNpTaxByLastDate(Integer lastYear, Integer lastMonth) {
        return this.baseMapper.getSalNpTaxByLastDate(lastYear, lastMonth);
    }
}
