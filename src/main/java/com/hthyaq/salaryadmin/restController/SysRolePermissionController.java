package com.hthyaq.salaryadmin.restController;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.entity.SysPermission;
import com.hthyaq.salaryadmin.entity.SysRolePermission;
import com.hthyaq.salaryadmin.service.SysPermissionService;
import com.hthyaq.salaryadmin.service.SysRolePermissionService;
import com.hthyaq.salaryadmin.vo.LabelValueInteger;
import com.hthyaq.salaryadmin.vo.PermissionPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 给角色分配权限 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Api
@RestController
@RequestMapping("/sysRolePermission")
public class SysRolePermissionController {
    @Autowired
    SysRolePermissionService sysRolePermissionService;
    @Autowired
    SysPermissionService sysPermissionService;

    @ApiOperation("保存-角色+权限")
    @PostMapping("/save")
    public boolean save(@RequestBody Map<String, Object> map) {
        Integer roleId = (Integer) map.get("roleId");
        ArrayList<Integer> permissionIds = (ArrayList<Integer>) JSONObject.parseArray(map.get("permissionIds").toString(), Integer.class);
        return sysRolePermissionService.deleteAndSave(roleId, permissionIds);
    }

    @ApiOperation("反显数据-角色+权限")
    @GetMapping("/editView")
    public List<PermissionPage> editView(String roleId) {
        List<PermissionPage> permissionPageList = Lists.newArrayList();
        //根据roleId查询出permission_id
        HashSet<Integer> permissionIds = new HashSet<>();
        List<SysRolePermission> listRolePermission = sysRolePermissionService.list(new QueryWrapper<SysRolePermission>().eq("role_id", roleId));
        listRolePermission.forEach(sysRolePermission -> permissionIds.add(sysRolePermission.getPermissionId()));
        //查询出权限中的导航菜单
        List<SysPermission> sysPermissionNav = sysPermissionService.list(new QueryWrapper<SysPermission>().eq("type", Constants.TYPE_NAV).ne("pid", 0).orderByAsc("sort"));
        HashMap<Integer, String> navs = Maps.newHashMap();
        sysPermissionNav.forEach(sysPermission -> {
            PermissionPage permissionPage = new PermissionPage();
            permissionPage.setId(sysPermission.getId())
                          .setName(sysPermission.getName())
                          .setPname(sysPermission.getPname());
            //查询出权限中的操作按钮
            List<SysPermission> sysPermissionButton = sysPermissionService.list(new QueryWrapper<SysPermission>().eq("pid", sysPermission.getId()).orderByAsc("sort"));
            sysPermissionButton.forEach(permission -> {
                Integer id = permission.getId();
                if (permissionIds.contains(id)) {
                    permissionPage.getCheckValues().add(id);
                }
                permissionPage.getPermissionList().add(new LabelValueInteger(permission.getName(), id));
            });
            permissionPageList.add(permissionPage);
        });
        return permissionPageList;
    }
}
