package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.entity.SysRolePermission;
import com.hthyaq.salaryadmin.mapper.SysRolePermissionMapper;
import com.hthyaq.salaryadmin.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 给角色分配权限 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {

    @Override
    public boolean deleteAndSave(Integer roleId, ArrayList<Integer> permissionIds) {
        boolean flag=true;
        //先删除
        flag=this.remove(new QueryWrapper<SysRolePermission>().eq("role_id",roleId));
        //后保存
        if(flag){
            List<SysRolePermission> sysRolePermissions= Lists.newArrayList();
            permissionIds.forEach(permissionId->{
                SysRolePermission sysRolePermission=new SysRolePermission();
                sysRolePermission.setRoleId(roleId).setPermissionId(permissionId);
                sysRolePermissions.add(sysRolePermission);
            });
            flag=flag && this.saveBatch(sysRolePermissions);
        }
        return flag;
    }
}
