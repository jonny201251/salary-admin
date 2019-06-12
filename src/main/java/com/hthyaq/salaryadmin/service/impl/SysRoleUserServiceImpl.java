package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.entity.SysRoleUser;
import com.hthyaq.salaryadmin.mapper.SysRoleUserMapper;
import com.hthyaq.salaryadmin.service.SysRoleUserService;
import com.hthyaq.salaryadmin.vo.RoleAndUserName;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 给角色绑定用户 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Service
public class SysRoleUserServiceImpl extends ServiceImpl<SysRoleUserMapper, SysRoleUser> implements SysRoleUserService {

    @Override
    public boolean deleteAndSave(Integer roleId, List<Integer> userIds) {
        boolean flag=true;
        //先删除
        flag=this.remove(new QueryWrapper<SysRoleUser>().eq("role_id",roleId));
        //后保存
        if(flag){
            List<SysRoleUser> sysRoleUsers= Lists.newArrayList();
            userIds.forEach(userId->{
                SysRoleUser sysRoleUser=new SysRoleUser();
                sysRoleUser.setRoleId(roleId).setUserId(userId);
                sysRoleUsers.add(sysRoleUser);
            });
            flag=flag && this.saveBatch(sysRoleUsers);
        }
        return flag;
    }

    @Override
    public List<RoleAndUserName> getProcessUserNames() {
        return this.baseMapper.getProcessUserNames();
    }

    @Override
    public List<String> getRoleNames(Integer userId) {
        return this.baseMapper.getRoleNames(userId);
    }
}
