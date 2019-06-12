package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.vo.SalNpPageData;

import java.util.List;

/**
 * <p>
 * 工资-内聘表 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
public interface SalNpService extends IService<SalNp> {
    boolean saveOrUpdateComplexData(SalNpPageData salNpPageData);
    SalNpPageData editViewComplexData(Long salNpId);
    boolean completeMonthSettlement();
    int updateFinishState(Integer year,Integer month);
    void onlyComputeNoTransactionForYingfa(SalNp salNp);
    void onlyComputeNoTransactionForFinish(SalNp salNp);
    void onlyComputeNoTransactionForOtherBonus(SalNp salNp, List<SalBonus> allSalBonus);
    void onlyComputeNoTransactionForJishui(SalNp salNp, List<SalNpTax> allSalNpTax);
}
