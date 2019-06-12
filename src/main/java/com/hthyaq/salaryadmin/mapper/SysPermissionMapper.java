package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.SysPermission;

import java.util.List;

/**
 * <p>
 * 权限表，别名资源表、菜单表 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<SysPermission> getMenuData(Integer userId);
    List<SysPermission> getButtonData(Integer userId);
}
