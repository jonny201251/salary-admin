package com.hthyaq.salaryadmin.util.salNpCalculate;

import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalNpService;
import com.hthyaq.salaryadmin.service.SalNpTaxService;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.TaxDeduction;

import java.util.List;
import java.util.Map;

//站发工资的计算
public class ZhanSalaryCalculate extends SalaryCalculate {
    public ZhanSalaryCalculate(SalNpService salNpService, SalBonusService salBonusService, SalNpTaxService salNpTaxService, SalNp salNp) {
        super(salNpService, salBonusService, salNpTaxService, salNp);
    }

    @Override
    public Double shouldTaxSum(String type, Double currentOtherBonusSum, Double currentJishuiAddSum, Double currentJishuisubtractSum, String flag) {
        if (Constants.SHUIKUAN1.equals(flag)) {
            return shouldTaxSum1(type, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum);
        }
        return 0.0;
    }

    /*
            type=页面，currentOtherBonusSum，currentJishuiAddSum,currentJishuisubtractSum需要被传递过来
            type=应发应扣,currentOtherBonusSum，currentJishuiAddSum,currentJishuisubtractSum需要被查询出来
            type=月结，currentOtherBonusSum=0,currentJishuiAddSum（食补=500）,currentJishuisubtractSum（基本扣除项=月份*5000）固定值
            type=其他薪金,currentOtherBonusSum需要被传递过来，currentJishuiAddSum,currentJishuisubtractSum需要被查询出来
            type=计税专用项,currentJishuiAddSum,currentJishuisubtractSum需要被传递过来，currentOtherBonusSum需要被查询出来
         */
    public Double shouldTaxSum1(String type, Double currentOtherBonusSum, Double currentJishuiAddSum, Double currentJishuisubtractSum) {
        double shouldTaxSum1 = 0.0, currentYingfaSum = 0.0, currentYingkouSum = 0.0;
        SalNp ss = salNp;
        currentYingfaSum = yingfa();
        currentYingkouSum = yingkou();
        if ("页面".equals(type)) {
            //
        } else if ("应发应扣".equals(type)) {
            Object obj1 = salBonusOrSalNpTaxBySalId(SalBonus.class);
            List<SalBonus> SalBonusList = (List<SalBonus>) obj1;
            currentOtherBonusSum = otherBonusSum(SalBonusList, Constants.YINGFA_TAX);
            Object obj2 = salBonusOrSalNpTaxBySalId(SalNpTax.class);
            List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj2;
            currentJishuiAddSum = jishuiSum(salNpTaxList, Constants.ADD);
            currentJishuisubtractSum = jishuiSum(salNpTaxList, Constants.SUBTRACT);
        } else if ("月结".equals(type)) {
            currentOtherBonusSum = 0.0;
            currentJishuiAddSum = 500.0;
            currentJishuisubtractSum = salNp.getMonth() * 5000.0;
        } else if ("其他薪金".equals(type)) {
            Object obj = salBonusOrSalNpTaxBySalId(SalNpTax.class);
            List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj;
            currentJishuiAddSum = jishuiSum(salNpTaxList, Constants.ADD);
            currentJishuisubtractSum = jishuiSum(salNpTaxList, Constants.SUBTRACT);
        } else if ("计税专用项".equals(type)) {
            Object obj = salBonusOrSalNpTaxBySalId(SalBonus.class);
            List<SalBonus> SalBonusList = (List<SalBonus>) obj;
            currentOtherBonusSum = otherBonusSum(SalBonusList, Constants.YINGFA_TAX);
        }
        Map<String, Double> beforeSumMap = beforeSum(1);
        shouldTaxSum1 = currentYingfaSum + beforeSumMap.get("beforeYingfaSum")
                + currentJishuiAddSum + beforeSumMap.get("beforeJishuiAddSum")
                + currentOtherBonusSum + beforeSumMap.get("beforeOtherBonusSum")
                - currentYingkouSum - beforeSumMap.get("beforeYingkouSum")
                - currentJishuisubtractSum - beforeSumMap.get("beforeJishuiSubtractSumExclude");
        return shouldTaxSum1;
    }

    @Override
    public Double shuikuan1(Double shouldTaxSum1) {
        Double shuikuan1 = 0.0;
        Map<String, Double> beforeSumMap = beforeSum(1);
        Double beforeShuikuan1Sum = (Double) beforeSumMap.get("beforeShuikuan1Sum");
        shuikuan1 = TaxDeduction.get(shouldTaxSum1) - beforeShuikuan1Sum;
        return shuikuan1 <= 0 ? 0.0 : shuikuan1;
    }

    @Override
    public Double shuikuan2(Double shouldTaxSum2) {
        return 0.0;
    }
}
