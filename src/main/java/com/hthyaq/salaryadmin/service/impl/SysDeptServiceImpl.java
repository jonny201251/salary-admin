package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hthyaq.salaryadmin.entity.*;
import com.hthyaq.salaryadmin.mapper.SysDeptMapper;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
    @Autowired
    SysDeptService sysDeptService;
    @Autowired
    ChangeSheetDeptService changeSheetDeptService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalLtxService salLtxService;


    @Override
    public boolean saveOrUpdateUserAndChangeSheet(SysDept newSysDept) {
        boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true;
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        Integer year = noFinishSalaryDate.getYear();
        Integer month = noFinishSalaryDate.getMonth();
        Integer id = newSysDept.getId();
        //id==null，表示是添加新部门
        if (id == null) {
            flag1 = this.save(newSysDept);
        } else {
            SysDept oldSysDept = sysDeptService.getById(id);
            String oldDeptName = oldSysDept.getName();
            String newDeptName = newSysDept.getName();
            if (!oldDeptName.equals(newDeptName)) {
                //部门名称变更
                QueryWrapper<ChangeSheetDept> queryWrapper = new QueryWrapper<ChangeSheetDept>().eq("sys_dept_id", oldSysDept.getId()).eq("year", year).eq("month", month);
                ChangeSheetDept changeSheetDept = changeSheetDeptService.getOne(queryWrapper);
                if (changeSheetDept == null) {
                    changeSheetDept = new ChangeSheetDept();
                    changeSheetDept.setYear(year)
                            .setMonth(month)
                            .setType(Constants.DEPT_CHANGE)
                            .setOldDept(oldDeptName)
                            .setNewDept(newDeptName)
                            .setReason(newSysDept.getComment())
                            .setStartDept(oldDeptName)
                            .setSysDeptId(oldSysDept.getId());
                } else {
                    changeSheetDept.setOldDept(changeSheetDept.getStartDept())
                            .setNewDept(newDeptName)
                            .setReason(newSysDept.getComment());
                }
                flag1 = changeSheetDeptService.save(changeSheetDept);
                flag2 = sysDeptService.updateById(newSysDept);
                //更新人员表的部门信息
                QueryWrapper<SysUser> queryWrapper2 = new QueryWrapper<SysUser>().eq("dept_id", oldSysDept.getId());
                List<SysUser> users = sysUserService.list(queryWrapper2);
                if (CollectionUtil.isNotNullOrEmpty(users)) {
                    users.forEach(user -> user.setDeptName(newDeptName));
                    flag3 = sysUserService.updateBatchById(users);
                }
                //更新人员表对应的工资的部门信息
                //更新-内聘
                QueryWrapper<SalNp> queryWrapper3 = new QueryWrapper<SalNp>().eq("year", year).eq("month", month).eq("user_dept_id", oldSysDept.getId()).eq("finish", Constants.FINISH_STATUS_NO);
                List<SalNp> salNps = salNpService.list(queryWrapper3);
                if (CollectionUtil.isNotNullOrEmpty(salNps)) {
                    salNps.forEach(salNp -> salNp.setUserDeptName(newDeptName));
                    flag4 = salNpService.updateBatchById(salNps);
                }
            } else {
                flag1 = this.updateById(newSysDept);
            }

        }
        return flag1 && flag2 && flag3 && flag4;
    }
}
