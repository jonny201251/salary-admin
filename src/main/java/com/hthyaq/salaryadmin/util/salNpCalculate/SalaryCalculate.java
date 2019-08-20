package com.hthyaq.salaryadmin.util.salNpCalculate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalNpService;
import com.hthyaq.salaryadmin.service.SalNpTaxService;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

/***
 * 站发工资、院发工资的计算
 */
public class SalaryCalculate {
    protected SalNpService salNpService;
    protected SalBonusService salBonusService;
    protected SalNpTaxService salNpTaxService;
    protected SalNp salNp;
    //食补
    static Map<Long, Double> eatMap = Maps.newHashMap();

    public SalaryCalculate(SalNpService salNpService, SalBonusService salBonusService, SalNpTaxService salNpTaxService, SalNp salNp) {
        this.salNpService = salNpService;
        this.salBonusService = salBonusService;
        this.salNpTaxService = salNpTaxService;
        this.salNp = salNp;
    }

    //食补
    public static void setEatMap(Map<Long, Double> tmp) {
        eatMap = tmp;
    }

    public Double getEatMoney() {
        Double money = eatMap.get(salNp.getLastId());
        return money == null ? 0.0 : money;
    }

    //获取一个工资计算的实例
    public static SalaryCalculate getInstance(SalNpService salNpService, SalBonusService salBonusService, SalNpTaxService salNpTaxService, SalNp salNp) {
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        SalaryCalculate salaryCalculate = null;
        if (Constants.GIVE_MODE_ZHAN.equals(salNp.getUserGiveMode()) || 1 == noFinishSalaryDate.getMonth()) {
            salaryCalculate = new ZhanSalaryCalculate(salNpService, salBonusService, salNpTaxService, salNp);
        } else {
            salaryCalculate = new YuanSalaryCalculate(salNpService, salBonusService, salNpTaxService, salNp);
        }
        return salaryCalculate;
    }

    //应发合计
    public Double yingfa() {
        return ofNullable(salNp.getGangwei()).orElse(0.0) +
                ofNullable(salNp.getXinji()).orElse(0.0) +
                ofNullable(salNp.getBucha()).orElse(0.0) +
                ofNullable(salNp.getBunei()).orElse(0.0) +
                ofNullable(salNp.getFudong()).orElse(0.0) +
                ofNullable(salNp.getHangling()).orElse(0.0) +
                ofNullable(salNp.getGongbu()).orElse(0.0) +
                ofNullable(salNp.getShuxi()).orElse(0.0) +
                ofNullable(salNp.getCaimo()).orElse(0.0) +
                ofNullable(salNp.getZhibu()).orElse(0.0) +
                ofNullable(salNp.getBaoliu()).orElse(0.0) +
                ofNullable(salNp.getJidu()).orElse(0.0) +
                ofNullable(salNp.getShuidian()).orElse(0.0) +
                ofNullable(salNp.getXiaoyi()).orElse(0.0) +
                ofNullable(salNp.getGangjin()).orElse(0.0) +
                ofNullable(salNp.getDanshengbu()).orElse(0.0) +
                ofNullable(salNp.getWuye()).orElse(0.0) +
                ofNullable(salNp.getQita()).orElse(0.0);
    }

    //应发合计
    public Double yingfa(SalNp salNp) {
        return ofNullable(salNp.getGangwei()).orElse(0.0) +
                ofNullable(salNp.getXinji()).orElse(0.0) +
                ofNullable(salNp.getBucha()).orElse(0.0) +
                ofNullable(salNp.getBunei()).orElse(0.0) +
                ofNullable(salNp.getFudong()).orElse(0.0) +
                ofNullable(salNp.getHangling()).orElse(0.0) +
                ofNullable(salNp.getGongbu()).orElse(0.0) +
                ofNullable(salNp.getShuxi()).orElse(0.0) +
                ofNullable(salNp.getCaimo()).orElse(0.0) +
                ofNullable(salNp.getZhibu()).orElse(0.0) +
                ofNullable(salNp.getBaoliu()).orElse(0.0) +
                ofNullable(salNp.getJidu()).orElse(0.0) +
                ofNullable(salNp.getShuidian()).orElse(0.0) +
                ofNullable(salNp.getXiaoyi()).orElse(0.0) +
                ofNullable(salNp.getGangjin()).orElse(0.0) +
                ofNullable(salNp.getDanshengbu()).orElse(0.0) +
                ofNullable(salNp.getWuye()).orElse(0.0) +
                ofNullable(salNp.getQita()).orElse(0.0);
    }

    //应扣合计(不包括税款1、税款2、房租、扣款)
    public Double yingkou() {
        return ofNullable(salNp.getYanglao()).orElse(0.0) +
                ofNullable(salNp.getNianjin()).orElse(0.0) +
                ofNullable(salNp.getZhufang()).orElse(0.0) +
                ofNullable(salNp.getShiye()).orElse(0.0) +
                ofNullable(salNp.getYiliao()).orElse(0.0);
    }

    //应扣合计(不包括税款1、税款2、房租、扣款)
    public Double yingkou(SalNp salNp) {
        return ofNullable(salNp.getYanglao()).orElse(0.0) +
                ofNullable(salNp.getNianjin()).orElse(0.0) +
                ofNullable(salNp.getZhufang()).orElse(0.0) +
                ofNullable(salNp.getShiye()).orElse(0.0) +
                ofNullable(salNp.getYiliao()).orElse(0.0);
    }

    //其他薪金合计,类别=应发计税、应发不计税、All
    public Double otherBonusSum(List<SalBonus> salBonuses, String type) {
        Double bonusSum = 0.0;
        if (CollectionUtil.isNotNullOrEmpty(salBonuses)) {
            for (SalBonus salBonus : salBonuses) {
                if (Constants.YINGFA_ALL.equals(type)) {
                    bonusSum += ofNullable(salBonus.getMoney()).orElse(0.0);
                } else if (salBonus.getType().equals(type)) {
                    bonusSum += ofNullable(salBonus.getMoney()).orElse(0.0);
                }
            }
        }
        return bonusSum;
    }

    //type=应发计税、应发不计税，即之前月份的其他薪金
    protected Double beforeOtherBonusSum(List<Long> beforeSalIdList, String type) {
        Double bonusSum = 0.0;
        if (salNp.getMonth() == 1) {
            return bonusSum;
        }
        List<SalBonus> otherBonusList = salBonusService.list(new QueryWrapper<SalBonus>().eq("type", type).eq("t_name", Constants.SAL_NP).in("sal_id", beforeSalIdList));
        if (CollectionUtil.isNotNullOrEmpty(otherBonusList)) {
            bonusSum = otherBonusList.stream().map(SalBonus::getMoney).reduce(0.0, Double::sum);
        }
        return bonusSum;
    }

    //计税专用-加项/减项，type=加项、减项
    public Double jishuiSum(List<SalNpTax> list, String type) {
        Double jishuiSum = 0.0;
        if (CollectionUtil.isNotNullOrEmpty(list)) {
            for (SalNpTax salNpTax : list) {
                if (type.equals(salNpTax.getType())) {
                    jishuiSum += ofNullable(salNpTax.getMoney()).orElse(0.0);
                }
            }
        }
        return jishuiSum;
    }

    //之前月份的计税专用-加项
    protected Double beforeJishuiAddSum(List<Long> beforeSalIdList) {
        Double beforeJishuiAddSum = 0.0;
        List<SalNpTax> salNpTaxList = salNpTaxService.list(new QueryWrapper<SalNpTax>().eq("type", Constants.ADD).in("sal_np_id", beforeSalIdList));
        if (CollectionUtil.isNotNullOrEmpty(salNpTaxList)) {
            beforeJishuiAddSum = salNpTaxList.stream().map(SalNpTax::getMoney).reduce(0.0, Double::sum);
        }
        return beforeJishuiAddSum;
    }

    //之前月份的计税专用-减项（除了Contants.TAX_COLUMNS之外的）
    protected Double beforeJishuiSubtractSumExclude(List<Long> beforeSalIdList) {
        Double beforeJishuiSubtractSumExclude = 0.0;
        List<SalNpTax> salNpTaxList = salNpTaxService.list(new QueryWrapper<SalNpTax>().eq("type", Constants.SUBTRACT).in("sal_np_id", beforeSalIdList).notIn("name", Constants.TAX_COLUMNS));
        if (CollectionUtil.isNotNullOrEmpty(salNpTaxList)) {
            beforeJishuiSubtractSumExclude = salNpTaxList.stream().map(SalNpTax::getMoney).reduce(0.0, Double::sum);
        }
        return beforeJishuiSubtractSumExclude;
    }

    //根据用户名查询出本年的[起始月份,当月)的工资
    protected List<SalNp> beforeSalNp(String userName, Integer startMonth) {
        return salNpService.list(new QueryWrapper<SalNp>().eq("year", salNp.getYear()).between("month", startMonth, salNp.getMonth() - 1).eq("user_name", userName));
    }

    /*
        根据salNpId查询出其他薪金、计税专用
        clazz=SalBonus.class、SalNpTax.class
     */
    public Object salBonusOrSalNpTaxBySalId(Class clazz) {
        Object obj = null;
        if (clazz == SalBonus.class) {
            obj = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_NP).eq("sal_id", salNp.getId()));
        } else {
            obj = salNpTaxService.list(new QueryWrapper<SalNpTax>().eq("sal_np_id", salNp.getId()));
        }
        return obj;
    }

    /*
    根据salNpId查询出其他薪金、计税专用
    clazz=SalBonus.class、SalNpTax.class
 */
    public Object salBonusOrSalNpTaxBySalId(Class clazz, Long salId) {
        Object obj = null;
        if (clazz == SalBonus.class) {
            obj = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_NP).eq("sal_id", salId));
        } else {
            obj = salNpTaxService.list(new QueryWrapper<SalNpTax>().eq("sal_np_id", salId));
        }
        return obj;
    }

    /*
    工资发放方式=站发工资
        [开始月份，当月)税款1、应发、应扣、其他薪金(类别=应发计税)、计税专用-加项、计税专用-减项（除了Contants.TAX_COLUMNS之外的）
        map.put("beforeShuikuan1Sum",beforeShuikuan1Sum);
        map.put("beforeYingfaSum", beforeYingfaSum);
        map.put("beforeYingkouSum", beforeYingkouSum);
        map.put("beforeOtherBonusSum", beforeOtherBonusSum);
        map.put("beforeJishuiAddSum", beforeJishuiAddSum);
        map.put("beforeJishuiSubtractSumExclude", beforeJishuiSubtractSumExclude);
    工资发放方式=院发工资
        税款1（其他薪金(类别=应发计税)、税款1）
        税款2（应发、应扣、计税专用-加项、税款2）
        map.put("beforeShuikuan1Sum",beforeShuikuan1Sum);
        map.put("beforeShuikuan2Sum",beforeShuikuan2Sum);
        map.put("beforeYingfaSum", beforeYingfaSum);
        map.put("beforeYingkouSum", beforeYingkouSum);
        map.put("beforeOtherBonusSum", beforeOtherBonusSum);
        map.put("beforeJishuiAddSum", beforeJishuiAddSum);
        map.put("beforeJishuiSubtractSumExclude", beforeJishuiSubtractSumExclude);
    */
    protected Map<String, Double> beforeSum(Integer startMonth) {
        Map<String, Double> beforeSumMap = Maps.newHashMap();
        Double beforeShuikuan1Sum = 0.0;
        Double beforeShuikuan2Sum = 0.0;
        Double beforeYingfaSum = 0.0;
        Double beforeYingkouSum = 0.0;
        Double beforeOtherBonusSum = 0.0;
        Double beforeJishuiAddSum = 0.0;
        Double beforeJishuiSubtractSumExclude = 0.0;
        List<SalNp> salNpList = beforeSalNp(salNp.getUserName(), startMonth);
        if (CollectionUtil.isNotNullOrEmpty(salNpList)) {
            //工资的id
            List<Long> beforeSalNpIds = Lists.newArrayList();
            for (SalNp salNp : salNpList) {
                beforeShuikuan1Sum += salNp.getShuikuan1();
                beforeShuikuan2Sum += salNp.getShuikuan2();
                beforeYingfaSum += salNp.getYingfa();
                beforeYingkouSum += salNp.getYingkou();
                beforeSalNpIds.add(salNp.getId());
            }
            //其他薪金(类别=应发计税)
            beforeOtherBonusSum = beforeOtherBonusSum(beforeSalNpIds, Constants.YINGFA_TAX);
            //计税专用-加项
            beforeJishuiAddSum = beforeJishuiAddSum(beforeSalNpIds);
            //计税专用-减项（除了Contants.TAX_COLUMNS之外的）
            beforeJishuiSubtractSumExclude = beforeJishuiSubtractSumExclude(beforeSalNpIds);
        }
        beforeSumMap.put("beforeShuikuan1Sum", beforeShuikuan1Sum);
        beforeSumMap.put("beforeShuikuan2Sum", beforeShuikuan2Sum);
        beforeSumMap.put("beforeYingfaSum", beforeYingfaSum);
        beforeSumMap.put("beforeYingkouSum", beforeYingkouSum);
        beforeSumMap.put("beforeOtherBonusSum", beforeOtherBonusSum);
        beforeSumMap.put("beforeJishuiAddSum", beforeJishuiAddSum);
        beforeSumMap.put("beforeJishuiSubtractSumExclude", beforeJishuiSubtractSumExclude);
        return beforeSumMap;
    }

    /*
        描述：累计应纳税所得额
        type
            页面：PC端添加或者修改工资
            应发应扣：提供应发应扣Excel模板，实现批量修改功能
            月结：用于月结时
            其他薪金：提供其他薪金Excel模板，实现批量修改功能
            计税专用项：提供计税专用项Excel模板，实现批量修改功能
        flag=税款1、税款2
     */
    public Double shouldTaxSum(String type, Double currentOtherBonusSum, Double currentJishuiAddSum, Double currentJishuisubtractSum, String flag) {
        return null;
    }

    /*
       工资发放方式=站发工资
            累计应纳税所得额=应发合计+[计税专用-加项]+[其他薪金（类别=应发计税）]-应扣合计(不包括税款、房租、扣款)-[计税专用项-减项]
            以上各项以本年的之前月份的数据+当前月份的数据，除了[计税专用项-减项]以当月的数据为准

            1月份
                税款1=计算（累计应纳税所得额）
            2月份
                税款1=计算（累计应纳税所得额）-1月份的税款1
            3月份
                税款1=计算（累计应纳税所得额）-1月份的税款1-2月份的税款1

            由以上所得，税款1=计算（累计应纳税所得额）-之前月份的税款1之和

       工资发放方式=站发工资
            累计应纳税所得额=应发合计+[计税专用-加项]+[其他薪金（类别=应发计税）]-应扣合计(不包括税款、房租、扣款)-[计税专用项-减项]
            以上各项以本年的之前月份的数据+当前月份的数据，除了[计税专用项-减项]以当月的数据为准

            1月份
                税款1=计算（累计应纳税所得额）
            2月份
                税款1=计算（累计应纳税所得额）-1月份的税款1
            3月份
                税款1=计算（累计应纳税所得额）-1月份的税款1-2月份的税款1

            由以上所得，税款1=计算（累计应纳税所得额）-之前月份的税款1之和

       工资发放方式=院发工资
            1月份金额（1月份的应纳税所得额）=应发合计+[计税专用-加项]+[其他薪金（类别=应发计税）]-应扣合计(养老保险+职业年金+住房+失业保险+医疗保险)-5000

			3月应纳税所得额=1月份的应纳税所得额+2月的其他薪金（类别=应发计税）+3月的其他薪金（类别=应发计税）-5000*(3-1)
			X月应纳税所得额=1月份的应纳税所得额+[2,X]月的其他薪金（类别=应发计税）-5000*(X-1)

            1月份的税款计算公式参考站发
            2月份以后的税款计算公式参考如下：
				当月的其他薪金
				[2月，当月)的其他薪金

				[2月，当月)的应发合计
				[2月，当月)的计税加项
				[2月，当月)的应扣合计
			累计应纳税所得额2=应发合计+计税加项+应扣合计-[当月的计税减项（全部项）]+2月份的手误3项（运行组加值班补助、论文优秀奖、2018年特殊岗位津贴）

            由以上所得，税款1=计算（X月应纳税所得额）-[1月，当月)的税款1
                        税款2=计算（累计应纳税所得额2）-[2月，当月)的税款2
     */
    public Double shuikuan1(Double shouldTaxSum1) {
        return null;
    }

    public Double shuikuan2(Double shouldTaxSum2) {
        return null;
    }

}
