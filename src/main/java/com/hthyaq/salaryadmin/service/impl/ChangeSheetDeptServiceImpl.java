package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hthyaq.salaryadmin.entity.ChangeSheetDept;
import com.hthyaq.salaryadmin.entity.SysDept;
import com.hthyaq.salaryadmin.mapper.ChangeSheetDeptMapper;
import com.hthyaq.salaryadmin.service.ChangeSheetDeptService;
import com.hthyaq.salaryadmin.service.SysDeptService;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 变动单-部门，来源于内聘 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Service
public class ChangeSheetDeptServiceImpl extends ServiceImpl<ChangeSheetDeptMapper, ChangeSheetDept> implements ChangeSheetDeptService {
    @Autowired
    SysDeptService sysDeptService;

    @Override
    public boolean saveOrUpdateChangeSheetDept(ChangeSheetDept pageChangeSheetDept) {
        boolean flag = true;
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        QueryWrapper<ChangeSheetDept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("year", noFinishSalaryDate.getYear()).eq("month", noFinishSalaryDate.getMonth()).eq("type", Constants.DEPT_RECOMBINE);
        List<ChangeSheetDept> changeSheetDepts = this.list(queryWrapper);
        if (CollectionUtil.isNotNullOrEmpty(changeSheetDepts)) {
            //C,D-->E,F
            //X,Y->O,K
            //A,K-->O,P
            for (ChangeSheetDept dbChangeSheetDept : changeSheetDepts) {
                flag = flag && handleChangeSheetDept(pageChangeSheetDept, dbChangeSheetDept);
            }
        } else {
            //A,B-->C,D
            flag = newChangeSheetDept(pageChangeSheetDept);
        }
        return flag;
    }

    private boolean newChangeSheetDept(ChangeSheetDept pageChangeSheetDept) {
        boolean flag1 = true, flag2 = true;
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        pageChangeSheetDept.setYear(noFinishSalaryDate.getYear())
                .setMonth(noFinishSalaryDate.getMonth())
                .setType(Constants.DEPT_RECOMBINE)
                .setStartDept(pageChangeSheetDept.getOldDept());
        flag1 = this.save(pageChangeSheetDept);
        List<String> deptNames = CollectionUtil.differentList(pageChangeSheetDept.getOldDept(), pageChangeSheetDept.getNewDept());
        if (CollectionUtil.isNotNullOrEmpty(deptNames)) {
            flag2 = updateDeptStatus(deptNames, Constants.DISABLE);
        }
        return flag1 && flag2;
    }

    //更新部门的使用状态
    private boolean updateDeptStatus(List<String> deptNames, String status) {
        /**
         * 将部门的使用状态改为禁用
         * 这里遗留一个bug，如果部门名称有重复，需要手动更改部门的使用状态为正常
         */
        QueryWrapper<SysDept> qw = new QueryWrapper<>();
        int len = deptNames.size();
        for (int i = 0; i < len; i++) {
            qw.eq("name", deptNames.get(i));
            if (len > 1 && i != (len - 1)) qw.or();
        }
        List<SysDept> list = sysDeptService.list(qw);
        list.forEach(dept -> dept.setStatus(status));
        return sysDeptService.updateBatchById(list);
    }

    private boolean handleChangeSheetDept(ChangeSheetDept pageChangeSheetDept, ChangeSheetDept dbChangeSheetDept) {
        boolean flag = true;
        String pageOldDept = pageChangeSheetDept.getOldDept();
        String pageNewDept = pageChangeSheetDept.getNewDept();
        String dbOldDept = dbChangeSheetDept.getOldDept();
        String dbNewDept = dbChangeSheetDept.getNewDept();
        if (CollectionUtil.isCollectionItemEqual(pageOldDept, dbNewDept)) {
            //C,D-->E,F
            dbChangeSheetDept.setOldDept(dbChangeSheetDept.getStartDept())
                    .setNewDept(pageNewDept)
                    .setReason(pageChangeSheetDept.getReason());
            flag = this.updateById(dbChangeSheetDept);
            flag = flag
                    && updateDeptStatus(Arrays.asList(dbOldDept.split(",")), Constants.NORMAL)
                    && updateDeptStatus(Arrays.asList(dbNewDept.split(",")), Constants.DISABLE);
        } else {
            //X,Y->O,K
            //A,K-->O,P
            flag = newChangeSheetDept(pageChangeSheetDept);
        }
        return flag;
    }
}
