package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SalLtx;
import com.hthyaq.salaryadmin.vo.SalLtxPageData;

/**
 * <p>
 * 工资离退休 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-10
 */
public interface SalLtxService extends IService<SalLtx> {

    boolean saveOrUpdateComplexData(SalLtxPageData salLtxPageData);

    SalLtxPageData editViewComplexData(Long salLtxId);

    boolean completeMonthSettlement();

    int updateFinishState(Integer year, Integer month);

    void compute(SalLtx salLtx);
}
