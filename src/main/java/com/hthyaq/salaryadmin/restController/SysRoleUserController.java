package com.hthyaq.salaryadmin.restController;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.TreeSelectOrTable;
import com.hthyaq.salaryadmin.entity.SysDept;
import com.hthyaq.salaryadmin.entity.SysRoleUser;
import com.hthyaq.salaryadmin.entity.SysUser;
import com.hthyaq.salaryadmin.service.SysDeptService;
import com.hthyaq.salaryadmin.service.SysRoleUserService;
import com.hthyaq.salaryadmin.service.SysUserService;
import com.hthyaq.salaryadmin.vo.BindUserDept;
import com.hthyaq.salaryadmin.vo.BindUserPage;
import com.hthyaq.salaryadmin.vo.LabelValueInteger;
import com.hthyaq.salaryadmin.vo.TreeSelectData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 给角色绑定用户 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Api
@RestController
@RequestMapping("/sysRoleUser")
public class SysRoleUserController {
    @Autowired
    SysRoleUserService sysRoleUserService;
    @Autowired
    SysDeptService sysDeptService;
    @Autowired
    SysUserService sysUserService;


    @ApiOperation("保存-用户+角色")
    @PostMapping("/save")
    public boolean save(@RequestBody Map<String, Object> map) {
        Integer roleId= (Integer) map.get("roleId");
        ArrayList<Integer> userIds= (ArrayList<Integer>) JSONObject.parseArray(map.get("userIds").toString(),Integer.class);
        return sysRoleUserService.deleteAndSave(roleId,userIds);
    }

    @ApiOperation("反显部门和用户的数据-用户+角色")
    @GetMapping("/editView")
    public BindUserPage editView(String roleId) {
        //根据roleId查询出user_id
        HashSet<Integer> userIds = new HashSet<>();
        List<SysRoleUser> listRoleUser = sysRoleUserService.list(new QueryWrapper<SysRoleUser>().eq("role_id", roleId));
        listRoleUser.forEach(sysRoleUser -> userIds.add(sysRoleUser.getUserId()));
        //所有部门查询出来
        List<SysDept> listDept = sysDeptService.list(new QueryWrapper<SysDept>().eq("status","正常").orderByAsc("sort"));
        //组装成部门树数据
        List<TreeSelectData> treeSelect = TreeSelectOrTable.getTreeSelect(listDept);
        //BindUserPage->treeSelectData
        BindUserPage bindUserPage = new BindUserPage();
        bindUserPage.setTreeSelectData(treeSelect);
        //遍历部门，将该部门中所有用户查询出来，同时判断出用户是否在用户-角色表中
        for (SysDept dept : listDept) {
            //BindUserPage->expandedKeys
            bindUserPage.getExpandedKeys().add(dept.getId() + "");
            if (!CollectionUtil.isNotNullOrEmpty(userIds)) continue;
            BindUserDept bindUserDept = new BindUserDept();
            //BindUserDept->deptId
            bindUserDept.setDeptId(dept.getId());
            bindUserDept.setDeptName(dept.getName());
            List<SysUser> sysUsers = sysUserService.list(new QueryWrapper<SysUser>().eq("dept_id", dept.getId()).orderByAsc("sort"));
            sysUsers.forEach(user -> {
                Integer userId = user.getId();
                //BindUserDept->users
                bindUserDept.getUsers().add(new LabelValueInteger(user.getName(), userId));
                if (userIds.contains(userId)) {
                    //BindUserDept->checkUserValues
                    bindUserDept.getCheckUserValues().add(userId);
                    //BindUserPage->checkedKeys-选定的部门
                    bindUserPage.getCheckedKeys().add(user.getDeptId() + "");
                }
            });
            //BindUserPage->userDeptList
            if(CollectionUtil.isNotNullOrEmpty(bindUserDept.getCheckUserValues())){
                bindUserPage.getUserDeptList().add(bindUserDept);
            }
        }
        return bindUserPage;
    }

    @ApiOperation("反显指定部门的用户的数据-用户+角色")
    @GetMapping("/editViewDept")
    public List<BindUserDept> editViewDept(String roleId,String deptIds) {
        List<Integer> depts= Lists.newArrayList();
        String[] ids=deptIds.split(",");
        for(String deptId:ids){
            depts.add(Integer.parseInt(deptId));
        }
        //根据roleId查询出user_id
        HashSet<Integer> userIds = new HashSet<>();
        List<SysRoleUser> listRoleUser = sysRoleUserService.list(new QueryWrapper<SysRoleUser>().eq("role_id", roleId));
        listRoleUser.forEach(sysRoleUser -> userIds.add(sysRoleUser.getUserId()));
        //指定部门查询出来
        List<SysDept> listDept = sysDeptService.list(new QueryWrapper<SysDept>().in("id",depts).orderByAsc("sort"));
        //遍历部门，将该部门中所有用户查询出来，同时判断出用户是否在用户-角色表中
        List<BindUserDept> userDeptList=Lists.newArrayList();
        for (SysDept dept : listDept) {
            BindUserDept bindUserDept = new BindUserDept();
            bindUserDept.setDeptId(dept.getId());
            bindUserDept.setDeptName(dept.getName());
            List<SysUser> sysUsers = sysUserService.list(new QueryWrapper<SysUser>().eq("dept_id", dept.getId()).orderByAsc("sort"));
            sysUsers.forEach(user -> {
                Integer userId = user.getId();
                bindUserDept.getUsers().add(new LabelValueInteger(user.getName(), userId));
                if (userIds.contains(userId)) {
                    bindUserDept.getCheckUserValues().add(userId);
                }
            });
            userDeptList.add(bindUserDept);
        }
        return userDeptList;
    }
}
