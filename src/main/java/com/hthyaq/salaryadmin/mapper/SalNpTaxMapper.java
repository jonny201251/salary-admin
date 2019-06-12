package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.SalNpTax;

import java.util.List;

/**
 * <p>
 * 计税专用项-工资内聘 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
public interface SalNpTaxMapper extends BaseMapper<SalNpTax> {
    List<SalNpTax> getSalNpTaxByUserName(List<String> names);
}
