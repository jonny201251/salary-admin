package com.hthyaq.salaryadmin.util.dateCache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hthyaq.salaryadmin.entity.SalLtx;
import com.hthyaq.salaryadmin.entity.SalLx;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.service.SalLtxService;
import com.hthyaq.salaryadmin.service.SalLxService;
import com.hthyaq.salaryadmin.service.SalNpService;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.SpringUtil;
import com.hthyaq.salaryadmin.util.YearMonth;

import java.util.List;
import java.util.concurrent.ExecutionException;

//将内聘工资、退休工资、离休工资的日期缓存起来
public class DateCacheUtil {
    private static LoadingCache<String, NoFinishSalaryDate> cache = CacheBuilder.newBuilder()
            .maximumSize(100)// 设置缓存个数
            .build(new CacheLoader<String, NoFinishSalaryDate>() {
                @Override
                //当本地缓存命没有中时，调用load方法获取结果并将结果缓存
                public NoFinishSalaryDate load(String key) {
                    return get(key);
                }

                // 数据库进行查询
                private NoFinishSalaryDate get(String key) {
                    if (Constants.SAL_NP.equals(key)) {
                        SalNpService salNpService = SpringUtil.getBean(SalNpService.class);
                        final List<SalNp> salNpList = salNpService.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO));
                        if (salNpList.size() == 0) {
                            return new NoFinishSalaryDate(YearMonth.getCurrentYear(), YearMonth.getCurrentMonth(), YearMonth.getYearMonthString(), YearMonth.getYearMonthInt());
                        }
                        SalNp salNp = salNpList.get(0);
                        return new NoFinishSalaryDate(salNp.getYear(), salNp.getMonth(), salNp.getYearmonthString(), salNp.getYearmonthInt());
                    } else if (Constants.SAL_LTX.equals(key)) {
                        SalLtxService salLtxService = SpringUtil.getBean(SalLtxService.class);
                        final List<SalLtx> salLtxList = salLtxService.list(new QueryWrapper<SalLtx>().eq("finish", Constants.FINISH_STATUS_NO));
                        if (salLtxList.size() == 0) {
                            return new NoFinishSalaryDate(YearMonth.getCurrentYear(), YearMonth.getCurrentMonth(), YearMonth.getYearMonthString(), YearMonth.getYearMonthInt());
                        }
                        SalLtx salLtx = salLtxList.get(0);
                        return new NoFinishSalaryDate(salLtx.getYear(), salLtx.getMonth(), salLtx.getYearmonthString(), salLtx.getYearmonthInt());
                    } else if (Constants.SAL_LX.equals(key)) {
                        SalLxService salLxService = SpringUtil.getBean(SalLxService.class);
                        final List<SalLx> salLxList = salLxService.list(new QueryWrapper<SalLx>().eq("finish", Constants.FINISH_STATUS_NO));
                        if (salLxList.size() == 0) {
                            return new NoFinishSalaryDate(YearMonth.getCurrentYear(), YearMonth.getCurrentMonth(), YearMonth.getYearMonthString(), YearMonth.getYearMonthInt());
                        }
                        SalLx salLx = salLxList.get(0);
                        return new NoFinishSalaryDate(salLx.getYear(), salLx.getMonth(), salLx.getYearmonthString(), salLx.getYearmonthInt());
                    } else {
                        throw new RuntimeException("月结时，key找不到了！");
                    }
                }
            });

    public static void set(String key, NoFinishSalaryDate noFinishSalaryDate) {
        cache.put(key, noFinishSalaryDate);
    }


    //key=salNp、salLtx、salLx
    public static NoFinishSalaryDate get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException("获取缓存中的工资日期时，出现了并发问题....");
        }
    }


}
