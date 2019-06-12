package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hthyaq.salaryadmin.entity.SysPermission;
import com.hthyaq.salaryadmin.mapper.SysPermissionMapper;
import com.hthyaq.salaryadmin.service.SysPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限表，别名资源表、菜单表 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
    @Override
    public List<SysPermission> getMenu(Integer userId) {
        return baseMapper.getMenuData(userId);
    }
    @Override
    public List<SysPermission> getButton(Integer userId) {
        return baseMapper.getButtonData(userId);
    }
}
