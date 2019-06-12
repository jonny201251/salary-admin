package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SysRoleUser;
import com.hthyaq.salaryadmin.vo.RoleAndUserName;

import java.util.List;

/**
 * <p>
 * 给角色绑定用户 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
public interface SysRoleUserService extends IService<SysRoleUser> {

    boolean deleteAndSave(Integer roleId, List<Integer> userIds);
    List<RoleAndUserName> getProcessUserNames();
    List<String> getRoleNames(Integer userId);
}
