package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.entity.*;
import com.hthyaq.salaryadmin.mapper.SalNpMapper;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.*;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.util.salNpCalculate.SalaryCalculate;
import com.hthyaq.salaryadmin.vo.Repeater;
import com.hthyaq.salaryadmin.vo.SalNpPageData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 工资-内聘表 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Service
public class SalNpServiceImpl extends ServiceImpl<SalNpMapper, SalNp> implements SalNpService {
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SalNpTaxService salNpTaxService;
    @Autowired
    ChangeSheetSalService changeSheetSalService;
    @Autowired
    SysUserService sysUserService;

    @Override
    public boolean saveOrUpdateComplexData(SalNpPageData salNpPageData) {
        boolean salFlag = true, computeFlag = true, changeSheetFlag = true;
        Long startSalId = salNpPageData.getId();
        //根据yearmonth_string抽取出year、month、yearmonth_int
        Map<String, Integer> yearmonth = YearMonth.getYearMonth(salNpPageData.getYearmonthString());
        salNpPageData.setYear(yearmonth.get(Constants.YEAR));
        salNpPageData.setMonth(yearmonth.get(Constants.MONTH));
        salNpPageData.setYearmonthInt(yearmonth.get(Constants.YEAR_MONTH_INT));
        //设置realMonth
        //工资-内聘表
        SalNp salNp = new SalNp();
        BeanUtils.copyProperties(salNpPageData, salNp);
        //填充用户信息
        fillUserInfo(salNp, salNpPageData.getUserName());
        if (startSalId == null) {
            //判断该人当月是否已创建了工资信息
            SalNp tmp = this.getOne(new QueryWrapper<SalNp>().eq("user_name", salNp.getUserName()).eq("year", salNp.getYear()).eq("month", salNp.getMonth()));
            if (tmp != null) throw new RuntimeException(salNp.getUserName() + "-已存在-" + salNp.getMonth() + "月份-工资信息！");
            //创建按钮-操作
            salFlag = this.save(salNp);
            if (!salFlag) throw new RuntimeException("[工资内聘]保存失败！");
            salNpPageData.setId(salNp.getId());
        }
        //工资计算
        computeFlag = compute(startSalId, salNpPageData);
        //保存真实工资数据
        BeanUtils.copyProperties(salNpPageData, salNp);
        salFlag = this.updateById(salNp);
        if (startSalId != null) {
            //编辑按钮-操作
            //查询出职工的上月工资
            Map<String, Integer> last = YearMonth.getLast(salNp.getYear(), salNp.getMonth());
            SalNp lastMonthSalNp = this.getOne(new QueryWrapper<SalNp>().eq("user_name", salNp.getUserName()).eq("year", last.get(Constants.LAST_YEAR)).eq("month", last.get(Constants.LAST_MONTH)));
            if (lastMonthSalNp != null) {
                //查询出已经变更的金额
                changeSheetFlag = insertChangeSheetSalData(lastMonthSalNp, salNp);
            }
        }
        return salFlag && computeFlag && changeSheetFlag;
    }


    @Override
    public SalNpPageData editViewComplexData(Long id) {
        SalNpPageData salNpPageData = new SalNpPageData();
        //工资-内聘表
        SalNp salNp = this.getById(id);
        BeanUtils.copyProperties(salNp, salNpPageData);
        //应发奖金或过节费表
        List<SalBonus> yingfajiangjin_repeater = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_NP).eq("sal_id", id));
        salNpPageData.setYingfajiangjin_repeater(new Repeater<>(yingfajiangjin_repeater));
        //计税专用项-加项
        List<SalNpTax> jishui_add_repeater = salNpTaxService.list(new QueryWrapper<SalNpTax>().eq("type", Constants.ADD).eq("sal_np_id", id));
        salNpPageData.setJishui_add_repeater(new Repeater<>(jishui_add_repeater));
        //计税专用项-减项
        List<SalNpTax> jishui_subtract_repeater = salNpTaxService.list(new QueryWrapper<SalNpTax>().eq("type", Constants.SUBTRACT).eq("sal_np_id", id));
        salNpPageData.setJishui_subtract_repeater(new Repeater<>(jishui_subtract_repeater));
        return salNpPageData;
    }

    /*
        月结时，只导入工资的应发和应扣款项，并且员工是在职状态的，不在职的就不导入了
    	应发、应扣、计税加项（食补=500）、计税减项(基本扣除项=月份*5000)
     */
    @Override
    public boolean completeMonthSettlement() {
        boolean flag = true;
        List<SalNp> list = this.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO).eq("user_job", Constants.USER_JOB));
        Integer dbYear = list.get(0).getYear();
        Integer dbMonth = list.get(0).getMonth();
        //获取工资表中的下个月的年份和月份
        Map<String, Integer> nextYearMonth = YearMonth.getNext(dbYear, dbMonth);
        Integer nextYear = nextYearMonth.get(Constants.NEXT_YEAR);
        Integer nextMonth = nextYearMonth.get(Constants.NEXT_MONTH);
        List<SalNp> newList = Lists.newArrayList();
        //食补
        Map<String, Integer> lastYearMonth = YearMonth.getLast(dbYear, dbMonth);
        Integer lastYear = lastYearMonth.get(Constants.LAST_YEAR);
        Integer lastMonth = lastYearMonth.get(Constants.LAST_MONTH);
        List<SalNpTax> salNpTaxList = salNpTaxService.getSalNpTaxByLastDate(lastYear, lastMonth);
        Map<Long, Double> eatMap = Maps.newHashMap();
        for (SalNpTax salNpTax : salNpTaxList) {
            eatMap.put(salNpTax.getSalNpId(), salNpTax.getMoney());
        }
        SalaryCalculate.setEatMap(eatMap);
        for (SalNp salNp : list) {
            //清空id,last_id
            salNp.setLastId(salNp.getId());
            salNp.setId(null);
            //给year,month,yearmonth_string,yearmonth_int,create_time重新赋值
            salNp.setYear(nextYear);
            salNp.setMonth(nextMonth);
            salNp.setYearmonthString(nextYear + "年" + nextMonth + "月");
            salNp.setYearmonthInt(YearMonth.getYearMonthInt(nextYear, nextMonth));
            salNp.setCreateTime(LocalDateTime.now());
            //清空comment
            salNp.setComment("");
            //设置realMonth
            if (nextMonth == 1) {
                salNp.setRealMonth(1);
            } else {
                salNp.setRealMonth(salNp.getRealMonth() + 1);
            }
            //重新计算应发合计、应扣合计、税款、实发
            this.onlyComputeNoTransactionForFinish(salNp);
            newList.add(salNp);
        }
        //插入下个月的工资数据
        if (CollectionUtil.isNotNullOrEmpty(newList)) {
            flag = this.saveBatch(newList);
        }
        if (flag) {
            //更新月结状态
            updateFinishState(dbYear, dbMonth);
            //日期的缓存更新
            DateCacheUtil.set(Constants.SAL_NP, new NoFinishSalaryDate(newList.get(0).getYear(), newList.get(0).getMonth(), newList.get(0).getYearmonthString(), newList.get(0).getYearmonthInt()));
        }
        return flag;
    }

    //将本月的工资数据的finish的未月结修改为已月结
    @Override
    public int updateFinishState(Integer year, Integer month) {
        return this.baseMapper.updateFinishState(year, month);
    }

    //页面
    private boolean compute(Long startSalId, SalNpPageData d) {
        if (startSalId != null) {
            //先删除
            salNpTaxService.remove(new QueryWrapper<SalNpTax>().eq("sal_np_id", startSalId));
            salBonusService.remove(new QueryWrapper<SalBonus>().eq("sal_id", startSalId));
        }
        //获取一个工资计算对象
        SalaryCalculate salaryCalculate = SalaryCalculate.getInstance(this, salBonusService, salNpTaxService, d);
        //应发合计
        Double yingfaSum = salaryCalculate.yingfa();
        //其他薪金合计(奖金合计)
        Double otherBonusAllSum = 0.0;
        boolean bonusFlag = true;
        List<SalBonus> yingfajiangjin_repeater = d.getYingfajiangjin_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(yingfajiangjin_repeater)) {
            for (SalBonus salBonus : yingfajiangjin_repeater) {
                salBonus.setTName(Constants.SAL_NP).setSalId(d.getId());
                otherBonusAllSum += ofNullable(salBonus.getMoney()).orElse(0.0);
            }
            bonusFlag = salBonusService.saveBatch(yingfajiangjin_repeater);
        }
        //应扣合计(不包括税款1、税款2、房租、扣款)
        Double yingkouSum = salaryCalculate.yingkou();
        //计税专用-加项
        boolean addFlag = true;
        List<SalNpTax> jishui_add_repeater = d.getJishui_add_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(jishui_add_repeater)) {
            for (SalNpTax salNpTax : jishui_add_repeater) {
                salNpTax.setType(Constants.ADD).setSalNpId(d.getId());
            }
            addFlag = salNpTaxService.saveBatch(jishui_add_repeater);
        }
        //计税专用项-减项
        boolean subtractFlag = true;
        List<SalNpTax> jishui_subtract_repeater = d.getJishui_subtract_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(jishui_subtract_repeater)) {
            for (SalNpTax salNpTax : jishui_subtract_repeater) {
                salNpTax.setType(Constants.SUBTRACT).setSalNpId(d.getId());
            }
            subtractFlag = salNpTaxService.saveBatch(jishui_subtract_repeater);
        }
        //累计应纳税所得额
        Double currentOtherBonusSum = salaryCalculate.otherBonusSum(yingfajiangjin_repeater, Constants.YINGFA_TAX);
        Double currentJishuiAddSum = salaryCalculate.jishuiSum(jishui_add_repeater, Constants.ADD);
        Double currentJishuisubtractSum = salaryCalculate.jishuiSum(jishui_subtract_repeater, Constants.SUBTRACT);
//        Double shouldTaxSum1 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_PAGE, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN1);
//        Double shouldTaxSum2 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_PAGE, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN2);
        //税款1
//        Double shuikuan1 = salaryCalculate.shuikuan1(shouldTaxSum1);
        Double shuikuan1 = ofNullable(d.getShuikuan1()).orElse(0.0);
        //税款2
//        Double shuikuan2 = salaryCalculate.shuikuan2(shouldTaxSum2);
        Double shuikuan2 = ofNullable(d.getShuikuan2()).orElse(0.0);

        shifa(d, yingfaSum, yingkouSum, shuikuan1, shuikuan2, otherBonusAllSum);
        return addFlag && bonusFlag && subtractFlag;
    }

    @Override
    public void onlyComputeNoTransactionForYingfa(SalNp salNp) {
        Long id = salNp.getId();
        //获取一个工资计算对象
        SalaryCalculate salaryCalculate = SalaryCalculate.getInstance(this, salBonusService, salNpTaxService, salNp);
        //应发合计
        Double yingfaSum = salaryCalculate.yingfa();
        //其他薪金
        Object obj1 = salaryCalculate.salBonusOrSalNpTaxBySalId(SalBonus.class);
        List<SalBonus> SalBonusList = (List<SalBonus>) obj1;
        Double otherBonusAllSum = salaryCalculate.otherBonusSum(SalBonusList, Constants.YINGFA_ALL);
        Double currentOtherBonusSum = salaryCalculate.otherBonusSum(SalBonusList, Constants.YINGFA_TAX);
        //应扣合计
        Double yingkouSum = salaryCalculate.yingkou();
        //计税专用项-加项、减项
        Object obj2 = salaryCalculate.salBonusOrSalNpTaxBySalId(SalNpTax.class);
        List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj2;
        Double currentJishuiAddSum = salaryCalculate.jishuiSum(salNpTaxList, Constants.ADD);
        Double currentJishuisubtractSum = salaryCalculate.jishuiSum(salNpTaxList, Constants.SUBTRACT);
        //应纳税所得额
//        Double shouldTaxSum1 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_YINGFAYINGKOU, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN1);
//        Double shouldTaxSum2 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_YINGFAYINGKOU, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN2);
        //税款1
//        Double shuikuan1 = salaryCalculate.shuikuan1(shouldTaxSum1);
        Double shuikuan1 = ofNullable(salNp.getShuikuan1()).orElse(0.0);
        //税款2
//        Double shuikuan2 = salaryCalculate.shuikuan2(shouldTaxSum2);
        Double shuikuan2 = ofNullable(salNp.getShuikuan2()).orElse(0.0);

        shifa(salNp, yingfaSum, yingkouSum, shuikuan1, shuikuan2, otherBonusAllSum);
    }

    @Override
    public void onlyComputeNoTransactionForFinish(SalNp salNp) {
        //获取一个工资计算对象
        SalaryCalculate salaryCalculate = SalaryCalculate.getInstance(this, salBonusService, salNpTaxService, salNp);
        //应发合计
        Double yingfaSum = salaryCalculate.yingfa();
        //应扣合计
        Double yingkouSum = salaryCalculate.yingkou();
        //应纳税所得额
//        Double shouldTaxSum1 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_YUEJIE, null, null, null, Constants.SHUIKUAN1);
//        Double shouldTaxSum2 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_YUEJIE, null, null, null, Constants.SHUIKUAN2);
        //税款1
//        Double shuikuan1 = salaryCalculate.shuikuan1(shouldTaxSum1);
        Double shuikuan1 = 0.0d;
        //税款2
//        Double shuikuan2 = salaryCalculate.shuikuan2(shouldTaxSum2);
        Double shuikuan2 = 0.0d;

        shifa(salNp, yingfaSum, yingkouSum, shuikuan1, shuikuan2, 0.0);
    }

    @Override
    public void onlyComputeNoTransactionForOtherBonus(SalNp salNp, List<SalBonus> allSalBonus) {
        //获取一个工资计算对象
        SalaryCalculate salaryCalculate = SalaryCalculate.getInstance(this, salBonusService, salNpTaxService, salNp);
        //应发合计
        Double yingfaSum = salaryCalculate.yingfa();
        //其他薪金
        Double otherBonusAllSum = salaryCalculate.otherBonusSum(allSalBonus, Constants.YINGFA_ALL);
        Double currentOtherBonusSum = salaryCalculate.otherBonusSum(allSalBonus, Constants.YINGFA_TAX);
        //应扣合计
        Double yingkouSum = salaryCalculate.yingkou();
        //计税专用项-加项、减项
        Object obj2 = salaryCalculate.salBonusOrSalNpTaxBySalId(SalNpTax.class);
        List<SalNpTax> salNpTaxList = (List<SalNpTax>) obj2;
        Double currentJishuiAddSum = salaryCalculate.jishuiSum(salNpTaxList, Constants.ADD);
        Double currentJishuisubtractSum = salaryCalculate.jishuiSum(salNpTaxList, Constants.SUBTRACT);
        //应纳税所得额
//        Double shouldTaxSum1 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_OTHER, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN1);
//        Double shouldTaxSum2 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_OTHER, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN2);
        //税款1
//        Double shuikuan1 = salaryCalculate.shuikuan1(shouldTaxSum1);
        Double shuikuan1 = ofNullable(salNp.getShuikuan1()).orElse(0.0);
        //税款2
//        Double shuikuan2 = salaryCalculate.shuikuan2(shouldTaxSum2);
        Double shuikuan2 = ofNullable(salNp.getShuikuan2()).orElse(0.0);

        shifa(salNp, yingfaSum, yingkouSum, shuikuan1, shuikuan2, otherBonusAllSum);
    }

    @Override
    public void onlyComputeNoTransactionForJishui(SalNp salNp, List<SalNpTax> allSalNpTax) {
        //获取一个工资计算对象
        SalaryCalculate salaryCalculate = SalaryCalculate.getInstance(this, salBonusService, salNpTaxService, salNp);
        //应发合计
        Double yingfaSum = salaryCalculate.yingfa();
        //其他薪金
        Object obj1 = salaryCalculate.salBonusOrSalNpTaxBySalId(SalBonus.class);
        List<SalBonus> SalBonusList = (List<SalBonus>) obj1;
        Double otherBonusAllSum = salaryCalculate.otherBonusSum(SalBonusList, Constants.YINGFA_ALL);
        Double currentOtherBonusSum = salaryCalculate.otherBonusSum(SalBonusList, Constants.YINGFA_TAX);
        //应扣合计
        Double yingkouSum = salaryCalculate.yingkou();
        //计税专用项-加项、减项
        Double currentJishuiAddSum = salaryCalculate.jishuiSum(allSalNpTax, Constants.ADD);
        Double currentJishuisubtractSum = salaryCalculate.jishuiSum(allSalNpTax, Constants.SUBTRACT);
        //应纳税所得额
//        Double shouldTaxSum1 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_TAX, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN1);
//        Double shouldTaxSum2 = salaryCalculate.shouldTaxSum(Constants.SALARY_CALCULATE_TYPE_TAX, currentOtherBonusSum, currentJishuiAddSum, currentJishuisubtractSum, Constants.SHUIKUAN2);
        //税款1
//        Double shuikuan1 = salaryCalculate.shuikuan1(shouldTaxSum1);
        Double shuikuan1 = ofNullable(salNp.getShuikuan1()).orElse(0.0);
        //税款2
//        Double shuikuan2 = salaryCalculate.shuikuan2(shouldTaxSum2);
        Double shuikuan2 = ofNullable(salNp.getShuikuan2()).orElse(0.0);

        shifa(salNp, yingfaSum, yingkouSum, shuikuan1, shuikuan2, otherBonusAllSum);
    }

    //计算出实发工资，并设置实发工资等数据
    private void shifa(SalNp salNp, Double yingfaSum, Double yingkouSum, Double shuikuan1, Double shuikuan2, Double otherBonusAllSum) {
        //实发工资=应发合计-应扣小计-税款1-税款2-房租-扣款+其他薪金
        Double shifa = yingfaSum - yingkouSum - shuikuan1 - shuikuan2 - ofNullable(salNp.getFangzu()).orElse(0.0) - ofNullable(salNp.getKoukuan()).orElse(0.0) + otherBonusAllSum;
        salNp.setYingfa(yingfaSum);
        salNp.setYingkou(yingkouSum);
        salNp.setJiangjin(otherBonusAllSum);
        salNp.setShuikuan1(shuikuan1);
        salNp.setShuikuan2(shuikuan2);
        salNp.setShifa(shifa);
    }

    private boolean insertChangeSheetSalData(SalNp lastMonthSalNp, SalNp salNp) {
        boolean flag = true;
        //待插入的ChangeSheetSal
        List<ChangeSheetSal> insertList = Lists.newArrayList();
        List<CompareObjResult> compareObjResultList = CompareObj.get(lastMonthSalNp, salNp, "shuikuan1,shuikuan2,yingfa,yingkou,jiangjin,shifa");
        compareObjResultList.forEach(result -> {
            ChangeSheetSal changeSheetSal = new ChangeSheetSal();
            changeSheetSal.setYear(salNp.getYear())
                    .setMonth(salNp.getMonth())
                    .setUserSort(salNp.getUserSort())
                    .setDeptName(salNp.getUserDeptName())
                    .setName(salNp.getUserName())
                    .setCategory(salNp.getUserCategory())
                    .setJob(salNp.getUserJob())
                    .setChangeName(SalColumnToChinese.get(result.getName(), Constants.SAL_NP))
                    .setLastMoney((Double) result.getOldValue())
                    .setCurrentMoney((Double) result.getNewValue())
                    .setReason(salNp.getComment())
                    .setTName(Constants.SAL_NP)
                    .setSalId(salNp.getId());
            insertList.add(changeSheetSal);
        });
        //先删除
        QueryWrapper<ChangeSheetSal> queryWrapper = new QueryWrapper<ChangeSheetSal>().eq("year", salNp.getYear()).eq("month", salNp.getMonth()).eq("name", salNp.getUserName()).eq("t_name", Constants.SAL_NP);
        flag = changeSheetSalService.remove(queryWrapper);
        //后插入
        if (flag) {
            flag = changeSheetSalService.saveBatch(insertList);
        }
        return flag;
    }

    //填充salNp中的用户信息
    private void fillUserInfo(SalNp salNp, String userName) {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("name", userName));
        salNp.setUserId(sysUser.getId());
        salNp.setUserName(sysUser.getName());
        salNp.setUserNum(sysUser.getNum());
        salNp.setUserCategory(sysUser.getCategory());
        salNp.setUserIdNum(sysUser.getIdNum());
        salNp.setUserBankAccount(sysUser.getBankAccount());
        salNp.setUserStatus(sysUser.getStatus());
        salNp.setUserJob(sysUser.getJob());
        salNp.setUserGiveMode(sysUser.getGiveMode());
        salNp.setUserDeptId(sysUser.getDeptId());
        salNp.setUserDeptName(sysUser.getDeptName());
        salNp.setUserOrg(sysUser.getOrg());
        salNp.setUserSort(sysUser.getSort());
    }
}
