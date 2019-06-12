package com.hthyaq.salaryadmin.service;

import com.hthyaq.salaryadmin.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

/**
 * <p>
 * 给角色分配权限 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    boolean deleteAndSave(Integer roleId, ArrayList<Integer> permissionIds);
}
