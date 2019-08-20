package com.hthyaq.salaryadmin.util.salNpCalculate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import static java.util.Optional.ofNullable;

//院发工资的计算
public class YuanSalaryCalculate extends SalaryCalculate {
    public YuanSalaryCalculate(SalNpService salNpService, SalBonusService salBonusService, SalNpTaxService salNpTaxService, SalNp salNp) {
        super(salNpService, salBonusService, salNpTaxService, salNp);
    }

    @Override
    public Double shouldTaxSum(String type, Double currentOtherBonusSum, Double currentJishuiAddSum, Double currentJishuisubtractSum, String flag) {
        if (Constants.SHUIKUAN1.equals(flag)) {
            return shouldTaxSum1(type, currentOtherBonusSum);
        } else {
            return shouldTaxSum2(type, currentJishuiAddSum, currentJishuisubtractSum);
        }
    }

    /*
        type=页面，currentOtherBonusSum需要被传递过来
        type=应发应扣,currentOtherBonusSum需要被传递过来
        type=月结，currentOtherBonusSum=null
        type=其他薪金,currentOtherBonusSum需要被传递过来
        type=计税专用项,currentOtherBonusSum需要被传递过来
        以上五种type,currentJishuisubtractSum全部等于（基本扣除项=(月份-1)*5000），即固定值
    */
    //税款1的应纳税所得额=1月份的应纳税所得额+[2,当月]月的其他薪金（类别=应发计税）-5000*(当月-1)
    private Double shouldTaxSum1(String type, Double currentOtherBonusSum) {
        double shouldTaxSum1 = 0.0;
        double currentJishuisubtractSum = (salNp.getRealMonth() - 1) * 5000.0;
        if ("页面".equals(type)) {
            //
        } else if ("应发应扣".equals(type)) {
            //
        } else if ("月结".equals(type)) {
            currentOtherBonusSum = 0.0;
        } else if ("其他薪金".equals(type)) {
            //
        } else if ("计税专用项".equals(type)) {
            //
        }
        Double money1 = money1();
        Map<String, Double> beforeSumMap = beforeSum(2);
        shouldTaxSum1 = money1 + currentOtherBonusSum + beforeSumMap.get("beforeOtherBonusSum") - currentJishuisubtractSum;
        return shouldTaxSum1;
    }

    /*
    type=页面，currentJishuiAddSum,currentJishuisubtractSum需要被传递过来
    type=应发应扣,currentJishuiAddSum,currentJishuisubtractSum需要被查询出来
    type=月结，currentJishuiAddSum（食补=500）,currentJishuisubtractSum（基本扣除项=月份*5000）固定值
    type=其他薪金，currentJishuiAddSum,currentJishuisubtractSum需要被查询出来
    type=计税专用项,currentJishuiAddSum,currentJishuisubtractSum需要被传递过来
     */
    //税款2的应纳税所得额
    private Double shouldTaxSum2(String type, Double currentJishuiAddSum, Double currentJishuisubtractSum) {
        double shouldTaxSum2 = 0.0, currentYingfaSum = 0.0, currentYingkouSum = 0.0;
        currentYingfaSum = yingfa();
        currentYingkouSum = yingkou();
        if ("页面".equals(type)) {
            //
        } else if ("应发应扣".equals(type)) {
            Object obj2 = salBonusOrSalNpTaxBySalId(SalNpTax.class);
            List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj2;
            currentJishuiAddSum = jishuiSum(salNpTaxList, Constants.ADD);
            currentJishuisubtractSum = jishuiSum(salNpTaxList, Constants.SUBTRACT);
        } else if ("月结".equals(type)) {
            currentJishuiAddSum = getEatMoney();
            currentJishuisubtractSum = salNp.getRealMonth() * 5000.0;
        } else if ("其他薪金".equals(type)) {
            Object obj = salBonusOrSalNpTaxBySalId(SalNpTax.class);
            List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj;
            currentJishuiAddSum = jishuiSum(salNpTaxList, Constants.ADD);
            currentJishuisubtractSum = jishuiSum(salNpTaxList, Constants.SUBTRACT);
        } else if ("计税专用项".equals(type)) {
            //
        }
        Map<String, Double> beforeSumMap = beforeSum(2);
        double threeSum = threeSum();
        shouldTaxSum2 = currentYingfaSum + beforeSumMap.get("beforeYingfaSum")
                + currentJishuiAddSum + beforeSumMap.get("beforeJishuiAddSum")
                - currentYingkouSum - beforeSumMap.get("beforeYingkouSum")
                - currentJishuisubtractSum - beforeSumMap.get("beforeJishuiSubtractSumExclude")
                + threeSum;
        return shouldTaxSum2;
    }

    //2月份的手误3项（运行组加值班补助、论文优秀奖、2018年特殊岗位津贴）
    private Double threeSum() {
        double threeSum = 0.0;
        SalNp salNp2 = salNpService.getOne(new QueryWrapper<SalNp>().eq("year", salNp.getYear()).eq("month", 2).eq("user_name", salNp.getUserName()));
        if (salNp2 != null) {
            List<SalBonus> list = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_NP).eq("sal_id", salNp2.getId()).in("name", "运行组加值班补助", "论文优秀奖", "2018年特殊岗位津贴"));
            threeSum = otherBonusSum(list, Constants.YINGFA_ALL);
        }
        return threeSum;
    }

    @Override
    public Double shuikuan1(Double shouldTaxSum1) {
        Double shuikuan1 = 0.0;
        Map<String, Double> beforeSumMap = beforeSum(1);
        Double beforeShuikuan1Sum = (Double) beforeSumMap.get("beforeShuikuan1Sum");
        shuikuan1 = TaxDeduction.get(shouldTaxSum1) - beforeShuikuan1Sum;
        return shuikuan1 <= 0 ? 0.0 : shuikuan1;
    }

    //1月份金额（1月份的应纳税所得额）=应发合计+[计税专用-加项]+[其他薪金（类别=应发计税）]-应扣合计(养老保险+职业年金+住房+失业保险+医疗保险)-5000
    private Double money1() {
        Double sum = 0.0;
        SalNp salNp1 = salNpService.getOne(new QueryWrapper<SalNp>().eq("year", salNp.getYear()).eq("month", 1).eq("user_name", salNp.getUserName()));
        if (salNp1 != null) {
            Double yingfa = yingfa(salNp1);
            Double yingkou = ofNullable(salNp1.getYanglao()).orElse(0.0)
                    + ofNullable(salNp1.getNianjin()).orElse(0.0)
                    + ofNullable(salNp1.getZhufang()).orElse(0.0)
                    + ofNullable(salNp1.getNianjin()).orElse(0.0)
                    + ofNullable(salNp1.getShiye()).orElse(0.0)
                    + ofNullable(salNp1.getYiliao()).orElse(0.0);
            Object obj1 = salBonusOrSalNpTaxBySalId(SalBonus.class, salNp1.getId());
            List<SalBonus> SalBonusList = (List<SalBonus>) obj1;
            Double otherBonusSum = otherBonusSum(SalBonusList, Constants.YINGFA_TAX);
            Object obj2 = salBonusOrSalNpTaxBySalId(SalNpTax.class, salNp1.getId());
            List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj2;
            Double jishuiAddSum = jishuiSum(salNpTaxList, Constants.ADD);
            sum = yingfa + jishuiAddSum + otherBonusSum - yingkou - 5000;
        }
        return sum;
    }

    @Override
    public Double shuikuan2(Double shouldTaxSum2) {
        Double shuikuan2 = 0.0;
        Map<String, Double> beforeSumMap = beforeSum(2);
        Double beforeMonthCount = beforeSumMap.get("beforeMonthCount");
        Double beforeShuikuan2Sum = (Double) beforeSumMap.get("beforeShuikuan2Sum");
        Double money1 = money1();
        shuikuan2 = TaxDeduction.get(shouldTaxSum2) - beforeShuikuan2Sum;
        return shuikuan2 <= 0 ? 0.0 : shuikuan2;
    }
}
