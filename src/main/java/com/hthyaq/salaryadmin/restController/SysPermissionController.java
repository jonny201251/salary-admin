package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.TreeSelectOrTable;
import com.hthyaq.salaryadmin.entity.SysPermission;
import com.hthyaq.salaryadmin.service.SysPermissionService;
import com.hthyaq.salaryadmin.vo.Menu;
import com.hthyaq.salaryadmin.vo.MenuPage;
import com.hthyaq.salaryadmin.vo.TreeSelectData;
import com.hthyaq.salaryadmin.vo.TreeTableData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 权限表，别名资源表、菜单表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Api
@RestController
@RequestMapping("/sysPermission")
public class SysPermissionController {
    @Autowired
    private SysPermissionService sysPermissionService;


    @ApiOperation("根据分页查询出-权限")
    @GetMapping("/list")
    public Page<SysPermission> list(Integer pageNum, @RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String pname) {
        Page<SysPermission> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        sysPermissionService.page(pagination, new QueryWrapper<SysPermission>().orderByAsc("sort").like("name", name).like("pname", pname));
        return pagination;
    }

    @ApiOperation("根据antd的treeSelectData数据结构查询出权限")
    @GetMapping("/treeSelectData")
    public List<TreeSelectData> treeSelectData() {
        List<SysPermission> permissions = sysPermissionService.list(new QueryWrapper<SysPermission>().orderByAsc("pid", "sort"));
        return TreeSelectOrTable.getTreeSelect(permissions);
    }

    @ApiOperation("根据antd的treeTableData数据结构查询出权限")
    @GetMapping("/treeTableData")
    public List<TreeTableData> treeTableData() {
        List<SysPermission> permissions = sysPermissionService.list(new QueryWrapper<SysPermission>().orderByAsc("pid"));
        return TreeSelectOrTable.getTreeTable(permissions);
    }


    @ApiOperation("保存和修改-权限")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdatePermission(@RequestBody SysPermission permission) {
        permission.setCreateTime(LocalDateTime.now());
        return sysPermissionService.saveOrUpdate(permission);
    }

    @ApiOperation("根据userId查询出该用户拥有的权限菜单")
    @GetMapping("/navMenu")
    public MenuPage navMenu(Integer userId) {
        MenuPage menuPage = new MenuPage();
        //user_id-->permission_id
        HashSet<Integer> permissionIds = Sets.newHashSet();
        List<SysPermission> sysPermissions = sysPermissionService.getMenu(userId);
        sysPermissions.forEach(sysPermission -> permissionIds.add(sysPermission.getId()));
        //先查询出所有的pid为0的导航菜单
        List<SysPermission> sysPermissionList = sysPermissionService.list(new QueryWrapper<SysPermission>().eq("pid", 0).orderByAsc("sort"));
        sysPermissionList.forEach(sysPermission -> {
            //导航菜单
            Menu menu = new Menu();
            menu.setKey(sysPermission.getId() + "");
            menu.setName(sysPermission.getName());
            menu.setIcon(sysPermission.getIcon());
            //再查询出-子导航菜单
            List<SysPermission> sysPermissionList2 = sysPermissionService.list(new QueryWrapper<SysPermission>().orderByAsc("sort").eq("pid", sysPermission.getId()));
            sysPermissionList2.forEach(sysPermission2 -> {
                if (permissionIds.contains(sysPermission2.getId())) {
                    //操作按钮
                    Menu subMenu = new Menu();
                    subMenu.setKey(sysPermission2.getId() + "");
                    subMenu.setName(sysPermission2.getName());
                    subMenu.setUrl(sysPermission2.getUrl());
                    menu.getChildren().add(subMenu);
                }
            });
            if (menu.getChildren().size() > 0) {
                menuPage.getMenus().add(menu);
                menuPage.getDefaultOpenKeys().add(sysPermission.getId() + "");
            }
        });
        return menuPage;
    }
}
