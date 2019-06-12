package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SalLx;
import com.hthyaq.salaryadmin.vo.SalLxPageData;

/**
 * <p>
 * 工资离休 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2019-04-24
 */
public interface SalLxService extends IService<SalLx> {
    boolean saveOrUpdateComplexData(SalLxPageData salLxPageData);

    SalLxPageData editViewComplexData(Long salLxId);

    boolean completeMonthSettlement();

    int updateFinishState(Integer year, Integer month);

    void compute(SalLx salLx);
}
