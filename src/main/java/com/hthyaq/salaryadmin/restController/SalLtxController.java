package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.YearMonth;
import com.hthyaq.salaryadmin.entity.SalLtx;
import com.hthyaq.salaryadmin.service.SalLtxService;
import com.hthyaq.salaryadmin.service.SysRoleUserService;
import com.hthyaq.salaryadmin.vo.SalLtxPageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 工资离退休 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-10
 */
@Api
@RestController
@RequestMapping("/salLtx")
public class SalLtxController {
    @Autowired
    SalLtxService salLtxService;
    @Autowired
    SysRoleUserService sysRoleUserService;

    /***
     * @param loginUserId 当前登录人的id
     * @param name 查询的姓名
     */
    @ApiOperation("根据分页查询-退休工资")
    @GetMapping("/list")
    public Page<SalLtx> list(Integer pageNum, String loginUserId, @RequestParam(defaultValue = "") String name, String yearmonth) {
        QueryWrapper<SalLtx> qw = new QueryWrapper<>();
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
        Page<SalLtx> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        salLtxService.page(pagination, qw);
        return pagination;
    }

    @ApiOperation("保存和修改-退休工资")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateSalLtx(@RequestBody SalLtxPageData salLtxPageData) {
        //create_time
        salLtxPageData.setCreateTime(LocalDateTime.now());
        return salLtxService.saveOrUpdateComplexData(salLtxPageData);
    }

    @ApiOperation("编辑按钮的反显数据-退休工资")
    @GetMapping("/editView")
    public SalLtxPageData editView(Long salLtxId) {
        return salLtxService.editViewComplexData(salLtxId);
    }

    @ApiOperation("月结-退休工资")
    @PostMapping("/finish")
    public boolean finish() {
        return salLtxService.completeMonthSettlement();
    }
}
