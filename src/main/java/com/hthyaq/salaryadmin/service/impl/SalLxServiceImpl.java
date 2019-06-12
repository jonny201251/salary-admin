package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.entity.ChangeSheetSal;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalLx;
import com.hthyaq.salaryadmin.entity.SysUser;
import com.hthyaq.salaryadmin.mapper.SalLxMapper;
import com.hthyaq.salaryadmin.service.ChangeSheetSalService;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalLxService;
import com.hthyaq.salaryadmin.service.SysUserService;
import com.hthyaq.salaryadmin.util.*;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.vo.Repeater;
import com.hthyaq.salaryadmin.vo.SalLxPageData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 工资离休 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2019-04-24
 */
@Service
public class SalLxServiceImpl extends ServiceImpl<SalLxMapper, SalLx> implements SalLxService {
    @Autowired
    private SalBonusService salBonusService;
    @Autowired
    private ChangeSheetSalService changeSheetSalService;
    @Autowired
    SysUserService sysUserService;

    @Override
    public boolean saveOrUpdateComplexData(SalLxPageData salLxPageData) {
        boolean salFlag = true, computeFlag = true, changeSheetFlag = true;
        Long startSalId = salLxPageData.getId();
        //根据yearmonth_string抽取出year、month、yearmonth_int
        Map<String, Integer> yearmonth = YearMonth.getYearMonth(salLxPageData.getYearmonthString());
        salLxPageData.setYear(yearmonth.get(Constants.YEAR));
        salLxPageData.setMonth(yearmonth.get(Constants.MONTH));
        salLxPageData.setYearmonthInt(yearmonth.get(Constants.YEAR_MONTH_INT));
        //工资-离休表
        SalLx salLx = new SalLx();
        BeanUtils.copyProperties(salLxPageData, salLx);
        //填充用户信息
        fillUserInfo(salLx, salLxPageData.getUserName());
        if (startSalId == null) {
            //创建按钮-操作
            salFlag = this.save(salLx);
            if (!salFlag) throw new RuntimeException("[工资离退休]保存失败！");
            salLxPageData.setId(salLx.getId());
        }
        //工资计算
        computeFlag = compute(startSalId, salLxPageData);
        //保存真实工资数据
        BeanUtils.copyProperties(salLxPageData, salLx);
        salFlag = this.updateById(salLx);
        if (startSalId != null) {
            //编辑按钮-操作
            //查询出职工的上月工资
            Map<String, Integer> map = YearMonth.getLast();
            QueryWrapper<SalLx> queryWrapper = new QueryWrapper<SalLx>().eq("user_name", salLxPageData.getUserName()).eq("year", map.get("lastYear")).eq("month", map.get("lastMonth"));
            SalLx lastMonthSalLx = this.getOne(queryWrapper);
            if (lastMonthSalLx != null) {
                //查询出已经变更的金额
                changeSheetFlag = insertChangeSheetSalData(lastMonthSalLx, salLx);
            }
        }
        return salFlag && computeFlag && changeSheetFlag;
    }

    @Override
    public SalLxPageData editViewComplexData(Long id) {
        SalLxPageData salLxPageData = new SalLxPageData();
        //工资-离休表
        SalLx salLx = this.getById(id);
        BeanUtils.copyProperties(salLx, salLxPageData);
        //应发奖金或过节费表
        List<SalBonus> yingfajiangjin_repeater = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_LX).eq("sal_id", id));
        salLxPageData.setYingfajiangjin_repeater(new Repeater<>(yingfajiangjin_repeater));
        return salLxPageData;
    }

    //月结时，只导入应发和应扣款项,并且用户状态=不在职之离休
    @Override
    public boolean completeMonthSettlement() {
        boolean flag = true;
        List<SalLx> list = this.list(new QueryWrapper<SalLx>().eq("finish", Constants.FINISH_STATUS_NO).eq("user_job", Constants.USER_NOT_JOB_LX));
        Integer dbYear = list.get(0).getYear();
        Integer dbMonth = list.get(0).getMonth();
        //获取工资表中的下个月的年份和月份
        Map<String, Integer> nextYearMonth = YearMonth.getNext(dbYear, dbMonth);
        Integer nextYear = nextYearMonth.get(Constants.NEXT_YEAR);
        Integer nextMonth = nextYearMonth.get(Constants.NEXT_MONTH);
        System.out.println();
        List<SalLx> newList = Lists.newArrayList();
        for (SalLx SalLx : list) {
            //清空id,last_id
            SalLx.setId(null);
            SalLx.setLastId(null);
            //给year,month,yearmonth_string,yearmonth_int,create_time重新赋值
            SalLx.setYear(nextYear);
            SalLx.setMonth(nextMonth);
            SalLx.setYearmonthString(nextYear + "年" + nextMonth + "月");
            SalLx.setYearmonthInt(YearMonth.getYearMonthInt(nextYear, nextMonth));
            SalLx.setCreateTime(LocalDateTime.now());

            newList.add(SalLx);
        }
        //插入下个月的工资数据
        if (CollectionUtil.isNotNullOrEmpty(newList)) {
            flag = this.saveBatch(newList);
        }
        if (flag) {
            //更新月结状态
            updateFinishState(dbYear, dbMonth);
            //日期的缓存更新
            DateCacheUtil.set(Constants.SAL_LX, new NoFinishSalaryDate(newList.get(0).getYear(), newList.get(0).getMonth(), newList.get(0).getYearmonthString(), newList.get(0).getYearmonthInt()));
        }
        return flag;
    }

    //将本月的工资数据的finish的未月结修改为已月结
    @Override
    public int updateFinishState(Integer year, Integer month) {
        return this.baseMapper.updateFinishState(year, month);
    }

    @Override
    public void compute(SalLx salLx) {
        //应发合计
        Double yingfaSum = yingfaSum(salLx);
        //应扣合计
        Double yingkouSum = yingkouSum(salLx);
        //实发工资=应发合计-应扣合计
        Double shifa = yingfaSum - yingkouSum;

        salLx.setYingfa(yingfaSum);
        salLx.setYingkou(yingkouSum);
        salLx.setShifa(shifa);
    }

    private boolean compute(Long startSalId, SalLxPageData d) {
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
                salBonus.setTName(Constants.SAL_LX).setSalId(d.getId());
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

    private Double yingkouSum(SalLx d) {
        return ofNullable(d.getFangzu()).orElse(0.0) +
                ofNullable(d.getYingkouqita()).orElse(0.0);
    }

    private Double yingfaSum(SalLx d) {
        return ofNullable(d.getJiben()).orElse(0.0) +
                ofNullable(d.getButie1()).orElse(0.0) +
                ofNullable(d.getHuli()).orElse(0.0) +
                ofNullable(d.getDianhua()).orElse(0.0) +
                ofNullable(d.getJiaotong()).orElse(0.0) +
                ofNullable(d.getButie2()).orElse(0.0) +
                ofNullable(d.getShubao()).orElse(0.0) +
                ofNullable(d.getButie3()).orElse(0.0) +
                ofNullable(d.getTiaozi2()).orElse(0.0) +
                ofNullable(d.getBucha()).orElse(0.0) +
                ofNullable(d.getZengzi()).orElse(0.0) +
                ofNullable(d.getYingfaqita()).orElse(0.0);
    }

    private boolean insertChangeSheetSalData(SalLx lastMonthSalLx, SalLx salLx) {
        boolean flag = true;
        //待插入的ChangeSheetSal
        List<ChangeSheetSal> insertList = Lists.newArrayList();
        CompareObj.get(lastMonthSalLx, salLx, "yingfa,yingkou,jiangjin,shifa").forEach(result -> {
            ChangeSheetSal changeSheetSal = new ChangeSheetSal();
            changeSheetSal.setYear(salLx.getYear())
                    .setMonth(salLx.getMonth())
                    .setUserSort(salLx.getUserSort())
                    .setName(salLx.getUserName())
                    .setCategory(salLx.getUserCategory())
                    .setJob(salLx.getUserJob())
                    .setChangeName(SalColumnToChinese.get(result.getName(), Constants.SAL_LX))
                    .setLastMoney((Double) result.getOldValue())
                    .setCurrentMoney((Double) result.getNewValue())
                    .setReason(salLx.getComment())
                    .setTName(Constants.SAL_LX)
                    .setSalId(salLx.getId());
            insertList.add(changeSheetSal);
        });
        //先删除
        QueryWrapper<ChangeSheetSal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year", salLx.getYear()).eq("month", salLx.getMonth()).eq("name", salLx.getUserName()).eq("t_name", Constants.SAL_LX);
        flag = changeSheetSalService.remove(queryWrapper);
        //后插入
        if (flag) {
            flag = changeSheetSalService.saveBatch(insertList);
        }
        return flag;
    }

    //填充salLx中的用户信息
    private void fillUserInfo(SalLx salLx, String userName) {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("name", userName));
        salLx.setUserId(sysUser.getId());
        salLx.setUserName(sysUser.getName());
        salLx.setUserNum(sysUser.getNum());
        salLx.setUserCategory(sysUser.getCategory());
        salLx.setUserBankAccount(sysUser.getBankAccount());
        salLx.setUserStatus(sysUser.getStatus());
        salLx.setUserJob(sysUser.getJob());
        salLx.setUserGiveMode(sysUser.getGiveMode());
        salLx.setUserDeptId(sysUser.getDeptId());
        salLx.setUserDeptName(sysUser.getDeptName());
        salLx.setUserOrg(sysUser.getOrg());
        salLx.setUserSort(sysUser.getSort());
    }
}
