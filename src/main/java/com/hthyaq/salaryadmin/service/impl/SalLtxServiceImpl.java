package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.entity.ChangeSheetSal;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalLtx;
import com.hthyaq.salaryadmin.entity.SysUser;
import com.hthyaq.salaryadmin.mapper.SalLtxMapper;
import com.hthyaq.salaryadmin.service.ChangeSheetSalService;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalLtxService;
import com.hthyaq.salaryadmin.service.SysUserService;
import com.hthyaq.salaryadmin.util.*;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.vo.Repeater;
import com.hthyaq.salaryadmin.vo.SalLtxPageData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 工资离退休 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-10
 */
@Service
public class SalLtxServiceImpl extends ServiceImpl<SalLtxMapper, SalLtx> implements SalLtxService {
    @Autowired
    private SalBonusService salBonusService;
    @Autowired
    private ChangeSheetSalService changeSheetSalService;
    @Autowired
    SysUserService sysUserService;

    @Override
    public boolean saveOrUpdateComplexData(SalLtxPageData salLtxPageData) {
        boolean salFlag = true, computeFlag = true, changeSheetFlag = true;
        Long startSalId = salLtxPageData.getId();
        //根据yearmonth_string抽取出year、month、yearmonth_int
        Map<String, Integer> yearmonth = YearMonth.getYearMonth(salLtxPageData.getYearmonthString());
        salLtxPageData.setYear(yearmonth.get(Constants.YEAR));
        salLtxPageData.setMonth(yearmonth.get(Constants.MONTH));
        salLtxPageData.setYearmonthInt(yearmonth.get(Constants.YEAR_MONTH_INT));
        //工资-离退休表
        SalLtx salLtx = new SalLtx();
        BeanUtils.copyProperties(salLtxPageData, salLtx);
        //填充用户信息
        fillUserInfo(salLtx, salLtxPageData.getUserName());
        if (startSalId == null) {
            //创建按钮-操作
            salFlag = this.save(salLtx);
            if (!salFlag) throw new RuntimeException("[工资离退休]保存失败！");
            salLtxPageData.setId(salLtx.getId());
        }
        //工资计算
        computeFlag = compute(startSalId, salLtxPageData);
        //保存真实工资数据
        BeanUtils.copyProperties(salLtxPageData, salLtx);
        salFlag = this.updateById(salLtx);
        if (startSalId != null) {
            //编辑按钮-操作
            //查询出职工的上月工资
            Map<String, Integer> map = YearMonth.getLast();
            QueryWrapper<SalLtx> queryWrapper = new QueryWrapper<SalLtx>().eq("user_name", salLtxPageData.getUserName()).eq("year", map.get("lastYear")).eq("month", map.get("lastMonth"));
            SalLtx lastMonthSalLtx = this.getOne(queryWrapper);
            if (lastMonthSalLtx != null) {
                //查询出已经变更的金额
                changeSheetFlag = insertChangeSheetSalData(lastMonthSalLtx, salLtx);
            }
        }
        return salFlag && computeFlag && changeSheetFlag;
    }

    @Override
    public SalLtxPageData editViewComplexData(Long id) {
        SalLtxPageData salLtxPageData = new SalLtxPageData();
        //工资-离退休表
        SalLtx salLtx = this.getById(id);
        BeanUtils.copyProperties(salLtx, salLtxPageData);
        //应发奖金或过节费表
        List<SalBonus> yingfajiangjin_repeater = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_LTX).eq("sal_id", id));
        salLtxPageData.setYingfajiangjin_repeater(new Repeater<>(yingfajiangjin_repeater));
        return salLtxPageData;
    }

    //月结时，只导入应发和应扣款项,并且用户状态=不在职之退休
    @Override
    public boolean completeMonthSettlement() {
        boolean flag = true;
        List<SalLtx> list = this.list(new QueryWrapper<SalLtx>().eq("finish", Constants.FINISH_STATUS_NO).eq("user_job", Constants.USER_NOT_JOB_RETIRE));
        Integer dbYear = list.get(0).getYear();
        Integer dbMonth = list.get(0).getMonth();
        //获取工资表中的下个月的年份和月份
        Map<String, Integer> nextYearMonth = YearMonth.getNext(dbYear, dbMonth);
        Integer nextYear = nextYearMonth.get(Constants.NEXT_YEAR);
        Integer nextMonth = nextYearMonth.get(Constants.NEXT_MONTH);
        List<SalLtx> newList = Lists.newArrayList();
        for (SalLtx SalLtx : list) {
            //清空id,last_id
            SalLtx.setId(null);
            SalLtx.setLastId(null);
            //给year,month,yearmonth_string,yearmonth_int,create_time重新赋值
            SalLtx.setYear(nextYear);
            SalLtx.setMonth(nextMonth);
            SalLtx.setYearmonthString(nextYear + "年" + nextMonth + "月");
            SalLtx.setYearmonthInt(YearMonth.getYearMonthInt(nextYear, nextMonth));
            SalLtx.setCreateTime(LocalDateTime.now());

            newList.add(SalLtx);
        }
        //插入下个月的工资数据
        if (CollectionUtil.isNotNullOrEmpty(newList)) {
            flag = this.saveBatch(newList);
        }
        if (flag) {
            //更新月结状态
            updateFinishState(dbYear, dbMonth);
            //日期的缓存更新
            DateCacheUtil.set(Constants.SAL_LTX, new NoFinishSalaryDate(newList.get(0).getYear(), newList.get(0).getMonth(), newList.get(0).getYearmonthString(), newList.get(0).getYearmonthInt()));
        }
        return flag;
    }

    //将本月的工资数据的finish的未月结修改为已月结
    @Override
    public int updateFinishState(Integer year, Integer month) {
        return this.baseMapper.updateFinishState(year, month);
    }

    @Override
    public void compute(SalLtx salLtx) {
        //应发合计
        Double yingfaSum = yingfaSum(salLtx);
        //应扣合计
        Double yingkouSum = yingkouSum(salLtx);
        //实发工资=应发合计-应扣合计
        Double shifa = yingfaSum - yingkouSum;

        salLtx.setYingfa(yingfaSum);
        salLtx.setYingkou(yingkouSum);
        salLtx.setShifa(shifa);
    }

    private boolean compute(Long startSalId, SalLtxPageData d) {
        if (startSalId != null) {
            //先删除
            salBonusService.remove(new QueryWrapper<SalBonus>().eq("sal_id", startSalId));
        }
        //应发合计
        Double yingfaSum = yingfaSum(d);
        //其他薪金
        boolean bonusFlag = true;
        List<SalBonus> yingfajiangjin_repeater = d.getYingfajiangjin_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(yingfajiangjin_repeater)) {
            for (SalBonus salBonus : yingfajiangjin_repeater) {
                salBonus.setTName(Constants.SAL_LTX).setSalId(d.getId());
            }
            bonusFlag = salBonusService.saveBatch(yingfajiangjin_repeater);
        }
        //应扣合计
        Double yingkouSum = yingkouSum(d);
        //实发工资=应发合计-应扣合计
        Double shifa = yingfaSum - yingkouSum;
        if (bonusFlag) {
            d.setYingfa(yingfaSum);
            d.setYingkou(yingkouSum);
            d.setShifa(shifa);
        }
        return bonusFlag;
    }

    private Double yingkouSum(SalLtx d) {
        return ofNullable(d.getFangzu()).orElse(0.0) +
                ofNullable(d.getYingkouqita()).orElse(0.0);
    }

    private Double yingfaSum(SalLtx d) {
        return ofNullable(d.getJiben()).orElse(0.0) +
                ofNullable(d.getGuifan()).orElse(0.0) +
                ofNullable(d.getBaoliu()).orElse(0.0) +
                ofNullable(d.getButie()).orElse(0.0) +
                ofNullable(d.getShubao()).orElse(0.0) +
                ofNullable(d.getTizu()).orElse(0.0) +
                ofNullable(d.getTiao()).orElse(0.0) +
                ofNullable(d.getBucha()).orElse(0.0) +
                ofNullable(d.getZengzi()).orElse(0.0) +
                ofNullable(d.getWuye()).orElse(0.0) +
                ofNullable(d.getYingfaqita()).orElse(0.0);
    }

    private boolean insertChangeSheetSalData(SalLtx lastMonthSalLtx, SalLtx salLtx) {
        boolean flag = true;
        //待插入的ChangeSheetSal
        List<ChangeSheetSal> insertList = Lists.newArrayList();
        CompareObj.get(lastMonthSalLtx, salLtx, "yingfa,yingkou,jiangjin,shifa").forEach(result -> {
            ChangeSheetSal changeSheetSal = new ChangeSheetSal();
            changeSheetSal.setYear(salLtx.getYear())
                    .setMonth(salLtx.getMonth())
                    .setUserSort(salLtx.getUserSort())
                    .setName(salLtx.getUserName())
                    .setCategory(salLtx.getUserCategory())
                    .setJob(salLtx.getUserJob())
                    .setChangeName(SalColumnToChinese.get(result.getName(), Constants.SAL_LTX))
                    .setLastMoney((Double) result.getOldValue())
                    .setCurrentMoney((Double) result.getNewValue())
                    .setReason(salLtx.getComment())
                    .setTName(Constants.SAL_LTX)
                    .setSalId(salLtx.getId());
            insertList.add(changeSheetSal);
        });
        //先删除
        QueryWrapper<ChangeSheetSal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year", salLtx.getYear()).eq("month", salLtx.getMonth()).eq("name", salLtx.getUserName()).eq("t_name", Constants.SAL_LTX);
        flag = changeSheetSalService.remove(queryWrapper);
        //后插入
        if (flag) {
            flag = changeSheetSalService.saveBatch(insertList);
        }
        return flag;
    }

    //填充salLtx中的用户信息
    private void fillUserInfo(SalLtx salLtx, String userName) {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("name", userName));
        salLtx.setUserId(sysUser.getId());
        salLtx.setUserName(sysUser.getName());
        salLtx.setUserNum(sysUser.getNum());
        salLtx.setUserCategory(sysUser.getCategory());
        salLtx.setUserBankAccount(sysUser.getBankAccount());
        salLtx.setUserStatus(sysUser.getStatus());
        salLtx.setUserJob(sysUser.getJob());
        salLtx.setUserGiveMode(sysUser.getGiveMode());
        salLtx.setUserDeptId(sysUser.getDeptId());
        salLtx.setUserDeptName(sysUser.getDeptName());
        salLtx.setUserOrg(sysUser.getOrg());
        salLtx.setUserSort(sysUser.getSort());
    }
}
