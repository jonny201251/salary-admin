package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.SysRoleUser;
import com.hthyaq.salaryadmin.vo.RoleAndUserName;

import java.util.List;

/**
 * <p>
 * 给角色绑定用户 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
public interface SysRoleUserMapper extends BaseMapper<SysRoleUser> {
    List<RoleAndUserName> getProcessUserNames();
    List<String> getRoleNames(Integer userId);
}
