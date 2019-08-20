package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalNpService;
import com.hthyaq.salaryadmin.service.SalNpTaxService;
import com.hthyaq.salaryadmin.service.SysRoleUserService;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.YearMonth;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.vo.SalNpPageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 工资-内聘表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Api
@RestController
@RequestMapping("/salNp")
public class SalNpController {
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalNpTaxService salNpTaxService;
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SysRoleUserService sysRoleUserService;


    /***
     * @param loginUserId 当前登录人的id
     * @param name 查询的姓名
     */
    @ApiOperation("根据分页查询-内聘工资")
    @GetMapping("/list")
    public Page<SalNp> list(Integer pageNum, String loginUserId, @RequestParam(defaultValue = "") String name, String yearmonth) {
        QueryWrapper<SalNp> qw = new QueryWrapper<>();
        Set<String> roleNames = new HashSet<>(sysRoleUserService.getRoleNames(Integer.parseInt(loginUserId)));
        if (roleNames.contains("系统管理员") || roleNames.contains("人事专员") || roleNames.contains("人事确认专员") || roleNames.contains("财务专员")) {
            qw.like("user_name", name).orderByDesc("year", "month").orderByAsc("user_sort");
        } else {
            qw.eq("user_id", loginUserId).orderByDesc("year", "month").orderByAsc("user_sort");
        }
        if (!Strings.isNullOrEmpty(yearmonth)) {
            Map<String, Integer> map = YearMonth.getStartEndYearMonth(yearmonth);
            qw.between("yearmonth_int", map.get("start"), map.get("end"));
        }
        Page<SalNp> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        salNpService.page(pagination, qw);
        return pagination;
    }

    @ApiOperation("保存和修改-内聘工资")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateSalNp(@RequestBody SalNpPageData salNpPageData) {
        //create_time
        salNpPageData.setCreateTime(LocalDateTime.now());
        return salNpService.saveOrUpdateComplexData(salNpPageData);
    }

    @ApiOperation("编辑按钮的反显数据-内聘工资")
    @GetMapping("/editView")
    public SalNpPageData editView(Long salNpId) {
        return salNpService.editViewComplexData(salNpId);
    }

    //应发、应扣、计税加项（食补=500）、计税减项（基本扣除项=月份*5000、6项扣除-都等于0）
    @ApiOperation("月结-内聘工资")
    @GetMapping("/finish")
    public boolean finish() {
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        //是否可以月结
        if (!YearMonth.isFinish(noFinishSalaryDate.getYear(), noFinishSalaryDate.getMonth())) {
            //不可以月结
            return false;
        }
        //月结计算
        boolean flag = salNpService.completeMonthSettlement();
        if (flag) {
            List<SalNp> salNpList = salNpService.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO));
            List<String> taxColumns = Constants.TAX_COLUMNS;
            List<SalNpTax> taxList = Lists.newArrayList();
            //获取上月的食补
            Map<String, Integer> last = YearMonth.getLast(salNpList.get(0).getYear(), salNpList.get(0).getMonth());
            int lastYear = last.get(Constants.LAST_YEAR);
            int lastMonth = last.get(Constants.LAST_MONTH);
            List<SalNpTax> salNpTaxList = salNpTaxService.getSalNpTaxByLastDate(lastYear, lastMonth);
            Map<Long, Double> eatMap = Maps.newHashMap();
            for (SalNpTax salNpTax : salNpTaxList) {
                eatMap.put(salNpTax.getSalNpId(), salNpTax.getMoney());
            }
            for (SalNp salNp : salNpList) {
                for (String column : taxColumns) {
                    if ("食补".equals(column)) {
                        Double money = eatMap.get(salNp.getLastId());
                        if (money == null) {
                            money = 0.0;
                        }
                        taxList.add(new SalNpTax().setName(column).setType(Constants.ADD).setMoney(money).setSalNpId(salNp.getId()));
                    } else if ("基本扣除项".equals(column)) {
                        taxList.add(new SalNpTax().setName("基本扣除项").setType(Constants.SUBTRACT).setMoney(5000.0 * salNp.getRealMonth()).setSalNpId(salNp.getId()));
                    } else {
                        taxList.add(new SalNpTax().setName(column).setType(Constants.SUBTRACT).setMoney(0.0).setSalNpId(salNp.getId()));
                    }
                }
            }
            if (CollectionUtil.isNotNullOrEmpty(taxList)) {
                flag = salNpTaxService.saveBatch(taxList);
            }
            //删除 其他薪金、计税专用--金额0的时候的情况
            salBonusService.remove(new QueryWrapper<SalBonus>().eq("money", 0.0).notIn("name", Constants.TAX_COLUMNS));
            salNpTaxService.remove(new QueryWrapper<SalNpTax>().eq("money", 0.0).notIn("name", Constants.TAX_COLUMNS));
        }
        return flag;
    }

    //内聘、离休、退休工资,月结
    @GetMapping("/finishTip")
    public NoFinishSalaryDate finishTip(String pathname) {
        String tmp = pathname.toLowerCase();
        String key = null;
        if (tmp.contains("np")) {
            key = Constants.SAL_NP;
        } else if (tmp.contains("ltx")) {
            key = Constants.SAL_LTX;
        } else {
            key = Constants.SAL_LX;
        }
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(key);
        return noFinishSalaryDate;
    }
}
