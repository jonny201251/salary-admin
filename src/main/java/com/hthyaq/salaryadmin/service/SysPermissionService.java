package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SysPermission;

import java.util.List;

/**
 * <p>
 * 权限表，别名资源表、菜单表 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
public interface SysPermissionService extends IService<SysPermission> {
    List<SysPermission> getMenu(Integer userId);
    List<SysPermission> getButton(Integer userId);
}
