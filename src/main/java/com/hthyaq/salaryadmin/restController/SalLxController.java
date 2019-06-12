package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.hthyaq.salaryadmin.entity.SalLx;
import com.hthyaq.salaryadmin.service.SalLxService;
import com.hthyaq.salaryadmin.service.SysRoleUserService;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.YearMonth;
import com.hthyaq.salaryadmin.vo.SalLxPageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 工资离休 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2019-04-24
 */
@RestController
@RequestMapping("/salLx")
public class SalLxController {
    @Autowired
    SalLxService salLxService;
    @Autowired
    SysRoleUserService sysRoleUserService;
    @GetMapping("/list")
    public Page<SalLx> list(Integer pageNum, String loginUserId, @RequestParam(defaultValue = "") String name, String yearmonth) {
        QueryWrapper<SalLx> qw = new QueryWrapper<>();
        //根据loginUserId，查询出角色名称
        Set<String> roleNames=new HashSet<>(sysRoleUserService.getRoleNames(Integer.parseInt(loginUserId)));
        if(roleNames.contains("系统管理员") || roleNames.contains("人事专员") || roleNames.contains("人事确认专员") || roleNames.contains("财务专员")){
            qw.like("user_name", name).orderByDesc("year", "month").orderByAsc("user_sort");
        }else{
            qw.eq("user_id",loginUserId).orderByDesc("year", "month").orderByAsc("user_sort");
        }
        if (!Strings.isNullOrEmpty(yearmonth)) {
            Map<String, Integer> map = YearMonth.getStartEndYearMonth(yearmonth);
            qw.between("yearmonth_int", map.get("start"), map.get("end"));
        }
        Page<SalLx> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        salLxService.page(pagination, qw);
        return pagination;
    }

    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateSalLx(@RequestBody SalLxPageData salLxPageData) {
        //create_time
        salLxPageData.setCreateTime(LocalDateTime.now());
        return salLxService.saveOrUpdateComplexData(salLxPageData);
    }

    @GetMapping("/editView")
    public SalLxPageData editView(Long salLxId) {
        return salLxService.editViewComplexData(salLxId);
    }

    @PostMapping("/finish")
    public boolean finish() {
        return salLxService.completeMonthSettlement();
    }
}
