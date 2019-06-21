package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.entity.SysPermission;
import com.hthyaq.salaryadmin.entity.SysUser;
import com.hthyaq.salaryadmin.service.SysPermissionService;
import com.hthyaq.salaryadmin.service.SysUserService;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.Md5Util;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.vo.LoginPage;
import com.hthyaq.salaryadmin.vo.SalPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 人员信息表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Api
@RestController
@RequestMapping("/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysPermissionService sysPermissionService;

    @ApiOperation("根据分页查询-用户")
    @GetMapping("/list")
    public Page<SysUser> list(Integer pageNum, @RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String deptName) {
        Page<SysUser> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        sysUserService.page(pagination, new QueryWrapper<SysUser>().like("name", name).like("dept_name", deptName).orderByAsc("sort", "create_time"));
        return pagination;
    }

    @ApiOperation("保存和修改-用户")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateUser(@RequestBody SysUser user) {
        user.setCreateTime(LocalDateTime.now());
        return sysUserService.saveOrUpdateUserAndChangeSheet(user);
    }

    @ApiOperation("根据姓名或职工编号查询出用户,用于工资管理的创建页面-用户")
    @GetMapping("/get")
    public SalPage get(String nameOrNum, String type) {
        QueryWrapper<SysUser> qw = new QueryWrapper<SysUser>().ne("name", "admin").like("name", nameOrNum);
        if ("/salNp".equals(type)) {
            qw.in("job", "在职", "不在职之调出");
        } else if ("/salLtx".equals(type)) {
            qw.in("job", "不在职之离职", "不在职之退休");
        } else {
            qw.eq("job", "不在职之离休");

        }
        List<SysUser> list = sysUserService.list(qw);
        if (CollectionUtil.isNotNullOrEmpty(list) && list.size() == 1) {
            String key = null;
            if (type.contains("/salNp")) {
                key = Constants.SAL_NP;
            } else if (type.contains("/salLtx")) {
                key = Constants.SAL_LTX;
            } else {
                key = Constants.SAL_LX;
            }
            return SalPage.converter(list.get(0), DateCacheUtil.get(key).getYearmonthString());
        } else {
            throw new RuntimeException("姓名-输入错误！");
        }
    }

    @ApiOperation("登录验证")
    @GetMapping("/login")
    public LoginPage login(String name, String pwd) {
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("name", name).eq("pwd", Md5Util.encryPassword(pwd)));
        if (sysUser == null) {
            throw new RuntimeException(Constants.LOGIN_FAIL_INFO);
        } else {
            LoginPage loginPage = new LoginPage();
            loginPage.setUserId(sysUser.getId());
            loginPage.setUserName(sysUser.getName());
            //查询出权限按钮
            HashMap<String, List<String>> buttons = loginPage.getButtons();
            List<SysPermission> sysPermissionList = sysPermissionService.getButton(sysUser.getId());
            sysPermissionList.forEach(sysPermission -> {
                String url = sysPermission.getUrl();
                List<String> list = buttons.get(url);
                if (list == null) {
                    list = Lists.newArrayList();
                    buttons.put(url, list);
                }
                list.add(sysPermission.getName());
            });
            return loginPage;
        }
    }
}
