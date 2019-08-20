package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SalNpTax;

import java.util.List;

/**
 * <p>
 * 计税专用项-工资内聘 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
public interface SalNpTaxService extends IService<SalNpTax> {
    List<SalNpTax> getSalNpTaxByUserName(List<String> names);

    List<SalNpTax> getSalNpTaxByLastDate(Integer lastYear, Integer lastMonth);
}
