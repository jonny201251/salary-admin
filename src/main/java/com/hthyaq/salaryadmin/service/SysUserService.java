package com.hthyaq.salaryadmin.service;

import com.hthyaq.salaryadmin.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 人员信息表 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
public interface SysUserService extends IService<SysUser> {
    boolean saveOrUpdateUserAndChangeSheet(SysUser sysUser);
}
