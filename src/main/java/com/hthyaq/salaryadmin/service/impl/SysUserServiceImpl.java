package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hthyaq.salaryadmin.entity.*;
import com.hthyaq.salaryadmin.mapper.SysUserMapper;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.IDNumUtil;
import com.hthyaq.salaryadmin.util.Md5Util;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 人员信息表 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    ChangeSheetUserService changeSheetUserService;
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalLtxService salLtxService;
    @Autowired
    SalLxService salLxService;
    @Autowired
    ChangeSheetSalService changeSheetSalService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SalNpTaxService salNpTaxService;
    @Autowired
    SalBonusService salBonusService;

    @Override
    public boolean saveOrUpdateUserAndChangeSheet(SysUser newSysUser) {
        boolean flag = true;
        Integer id = newSysUser.getId();
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        //id==null，表示是添加新用户
        if (id == null) {
            //判断姓名是否存在
            SysUser tmp = sysUserService.getOne(new QueryWrapper<SysUser>().eq("name", newSysUser.getName()));
            if (tmp != null) {
                //存在，姓名=姓名+身份证号后3位
                newSysUser.setName(newSysUser.getName() + "" + IDNumUtil.idNum(newSysUser.getIdNum(), 3));
            }
            //密码=身份证号后6位
            newSysUser.setPwd(Md5Util.encryPassword(IDNumUtil.idNum(newSysUser.getIdNum(), 6)));
            //先保存用户，是为了获取用户的id
            flag = this.save(newSysUser);
            //变动单
            ChangeSheetUser changeSheetUser = new ChangeSheetUser();
            changeSheetUser.setYear(noFinishSalaryDate.getYear())
                    .setMonth(noFinishSalaryDate.getMonth())
                    .setType(Constants.USER_ADD)
                    .setName(newSysUser.getName())
                    .setNum(newSysUser.getNum())
                    .setNewDept(newSysUser.getDeptName())
                    .setReason(newSysUser.getComment())
                    .setSysUserId(newSysUser.getId());
            //人员变动-内聘之新增人员
            flag = flag && changeSheetUserService.save(changeSheetUser);
        } else {
            SysUser oldUser = this.getById(newSysUser.getId());
            flag = modifyUserGenerateChangeSheet(oldUser, newSysUser);
            //更新changeSheetUser中的排序
            if (oldUser.getSort() != newSysUser.getSort()) {
                ChangeSheetUser changeSheetUser = changeSheetUserService.getOne(new QueryWrapper<ChangeSheetUser>().eq("sys_user_id", newSysUser.getId()));
                if (changeSheetUser != null) {
                    changeSheetUser.setUserSort(newSysUser.getSort());
                    flag = changeSheetUserService.updateById(changeSheetUser);
                }
            }
            //更新人员的基本信息
            flag = flag && this.updateById(newSysUser);
        }
        return flag;
    }

    /***
     *通过修改用户信息从而产生的变动单
     * 人员变动-内聘
     *       在职-人员部门调动
     *      不在职之xxx
     *
     * 在职->调出+离职+死亡，删除当月的工资信息
     * 在职->退休，在职里删除当月的工资，退休里添加当月的工资（数据为0）
     * 在职 不会转 离休
     *
     * 内聘人员的部门调动------------------内聘人员的部门更新
     * 内聘人员的离职时的处理--------------内聘人员删除
     * 内聘人员的死亡时的处理-------------内聘人员删除
     * 内聘人员的调出时的处理-------------内聘人员删除
     * 内聘人员的退休时的处理----------内聘人员删除+退休中添加一条数据
     * 内聘人员的离休时的处理--------------内聘人员删除+离休中添加一条数据
     * 退休人员的死亡时的处理-------------退休人员删除
     * 离休人员的死亡时的处理----------离休人员删除
     *
     * 更新-工资的人员信息
     */
    public boolean modifyUserGenerateChangeSheet(SysUser oldSysUser, SysUser newSysUser) {
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        Integer year = noFinishSalaryDate.getYear();
        Integer month = noFinishSalaryDate.getMonth();
        boolean flag1 = true, flag2 = true;
        //新旧的在职状态
        String oldJob = oldSysUser.getJob();
        String newJob = newSysUser.getJob();
        //新旧的部门名称
        String oldDeptName = oldSysUser.getDeptName();
        String newDeptName = newSysUser.getDeptName();
        if (oldJob.equals(newJob) && Constants.USER_JOB.equals(newJob) && !oldDeptName.equals(newDeptName)) {
            //内聘人员的部门调动
            QueryWrapper<ChangeSheetUser> QueryWrapper = new QueryWrapper<>();
            QueryWrapper.eq("sys_user_id", oldSysUser.getId()).eq("year", year).eq("month", month);
            ChangeSheetUser changeSheetUser = changeSheetUserService.getOne(QueryWrapper);
            if (changeSheetUser == null) {
                changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_CHANGE, oldDeptName, newDeptName);
                flag1 = changeSheetUserService.save(changeSheetUser);
            } else {
                changeSheetUser.setName(newSysUser.getName())
                        .setUserSort(newSysUser.getSort())
                        .setOldDept(changeSheetUser.getStartDept())
                        .setNewDept(newDeptName)
                        .setReason(newSysUser.getComment());
                flag1 = changeSheetUserService.updateById(changeSheetUser);
            }
            //更新-工资的人员信息
            flag2 = updateSalNpUserInfo(oldSysUser, newSysUser);
        } else if (Constants.USER_JOB.equals(oldJob) && Constants.USER_NOT_JOB_LEAVE.equals(newJob)) {
            //内聘人员的离职时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_NOT_JOB_LEAVE, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalNpUserInfo(oldSysUser);
        } else if (Constants.USER_JOB.equals(oldJob) && Constants.USER_NOT_JOB_DIE.equals(newJob)) {
            //内聘人员的死亡时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_NOT_JOB_DIE, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalNpUserInfo(oldSysUser);
        } else if (Constants.USER_JOB.equals(oldJob) && Constants.USER_NOT_JOB_GO.equals(newJob)) {
            //内聘人员的调出时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_NOT_JOB_GO, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalNpUserInfo(oldSysUser);
        } else if (Constants.USER_JOB.equals(oldJob) && Constants.USER_NOT_JOB_RETIRE.equals(newJob)) {
            //内聘人员的退休时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_NOT_JOB_RETIRE, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalNpUserInfo(oldSysUser);
            flag2 = flag2 && insertSalLtx(oldSysUser);
        } else if (Constants.USER_NOT_JOB_RETIRE.equals(oldJob) && (Constants.USER_LTX_DIE.equals(newJob) || Constants.USER_NOT_JOB_DIE.equals(newJob))) {
            //退休人员的死亡时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_LTX_DIE, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalLtxUserInfo(oldSysUser);
        } else if (Constants.USER_JOB.equals(oldJob) && Constants.USER_NOT_JOB_LX.equals(newJob)) {
            //内聘人员的离休时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_NOT_JOB_LX, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalNpUserInfo(oldSysUser);
            flag2 = flag2 && insertSalLx(oldSysUser);
        } else if (Constants.USER_NOT_JOB_LX.equals(oldJob) && (Constants.USER_LTX_DIE.equals(newJob) || Constants.USER_NOT_JOB_DIE.equals(newJob))) {
            //离休人员的死亡时的处理
            ChangeSheetUser changeSheetUser = constructChangeSheetUser(oldSysUser, newSysUser, year, month, Constants.USER_LTX_DIE, oldDeptName, newDeptName);
            flag1 = changeSheetUserService.save(changeSheetUser);
            flag2 = deleteSalLxUserInfo(oldSysUser);
        } else {
            //更新-工资的人员信息
            if (oldJob.equals(newJob) && Constants.USER_JOB.equals(newJob)) {
                flag2 = updateSalNpUserInfo(oldSysUser, newSysUser);
            } else if (Constants.USER_NOT_JOB_LX.equals(newJob)) {
                flag2 = updateSalLxUserInfo(oldSysUser, newSysUser);
            } else if (Constants.USER_NOT_JOB_RETIRE.equals(newJob)) {
                flag2 = updateSalLtxUserInfo(oldSysUser, newSysUser);
            }
        }
        return flag1 && flag2;
    }

    //往sal_ltx(退休表)插入一条数据
    private boolean insertSalLtx(SysUser oldSysUser) {
        SalLtx salLtx = new SalLtx();
        salLtx.setUserId(oldSysUser.getId());
        salLtx.setUserName(oldSysUser.getName());
        salLtx.setUserCategory(oldSysUser.getCategory());
        salLtx.setUserBankAccount(oldSysUser.getBankAccount());
        salLtx.setUserStatus(oldSysUser.getStatus());
        salLtx.setUserJob(oldSysUser.getJob());
        salLtx.setUserGiveMode(oldSysUser.getGiveMode());
        salLtx.setUserSort(oldSysUser.getSort());
        salLtx.setUserDeptId(oldSysUser.getDeptId());
        salLtx.setUserDeptName(oldSysUser.getDeptName());
        salLtx.setUserOrg(oldSysUser.getOrg());
        //时间
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        salLtx.setYearmonthString(noFinishSalaryDate.getYearmonthString());
        salLtx.setYearmonthInt(noFinishSalaryDate.getYearmonthInt());
        salLtx.setYear(noFinishSalaryDate.getYear());
        salLtx.setMonth(noFinishSalaryDate.getMonth());
        return salLtxService.save(salLtx);
    }

    //往sal_lx(离休表)插入一条数据
    private boolean insertSalLx(SysUser oldSysUser) {
        SalLx salLx = new SalLx();
        salLx.setUserId(oldSysUser.getId());
        salLx.setUserName(oldSysUser.getName());
        salLx.setUserCategory(oldSysUser.getCategory());
        salLx.setUserBankAccount(oldSysUser.getBankAccount());
        salLx.setUserStatus(oldSysUser.getStatus());
        salLx.setUserJob(oldSysUser.getJob());
        salLx.setUserGiveMode(oldSysUser.getGiveMode());
        salLx.setUserSort(oldSysUser.getSort());
        salLx.setUserDeptId(oldSysUser.getDeptId());
        salLx.setUserDeptName(oldSysUser.getDeptName());
        salLx.setUserOrg(oldSysUser.getOrg());
        //时间
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        salLx.setYearmonthString(noFinishSalaryDate.getYearmonthString());
        salLx.setYearmonthInt(noFinishSalaryDate.getYearmonthInt());
        salLx.setYear(noFinishSalaryDate.getYear());
        salLx.setMonth(noFinishSalaryDate.getMonth());
        return salLxService.save(salLx);
    }

    //当在职人员处于[调出、死亡、离职]状态时，删除-工资的人员信息
    private boolean deleteSalNpUserInfo(SysUser oldSysUser) {
        boolean flag = true;
        SalNp salNp = salNpService.getOne(new QueryWrapper<SalNp>().eq("user_name", oldSysUser.getName()).eq("finish", Constants.FINISH_STATUS_NO));
        if (salNp != null) {
            //先删除
            salNpTaxService.remove(new QueryWrapper<SalNpTax>().eq("sal_np_id", salNp.getId()));
            salBonusService.remove(new QueryWrapper<SalBonus>().eq("sal_id", salNp.getId()));
            flag = salNpService.removeById(salNp.getId());
        }
        return flag;
    }

    private boolean deleteSalLtxUserInfo(SysUser oldSysUser) {
        boolean flag = true;
        SalLtx salLtx = salLtxService.getOne(new QueryWrapper<SalLtx>().eq("user_name", oldSysUser.getName()).eq("finish", Constants.FINISH_STATUS_NO));
        if (salLtx != null) {
            //先删除
            salBonusService.remove(new QueryWrapper<SalBonus>().eq("sal_id", salLtx.getId()));
            flag = salLtxService.removeById(salLtx.getId());
        }
        return flag;
    }

    private boolean deleteSalLxUserInfo(SysUser oldSysUser) {
        boolean flag = true;
        SalLx salLx = salLxService.getOne(new QueryWrapper<SalLx>().eq("user_name", oldSysUser.getName()).eq("finish", Constants.FINISH_STATUS_NO));
        if (salLx != null) {
            //先删除
            salBonusService.remove(new QueryWrapper<SalBonus>().eq("sal_id", salLx.getId()));
            flag = salLxService.removeById(salLx.getId());
        }
        return flag;
    }

    //构造ChangeSheetUser
    private ChangeSheetUser constructChangeSheetUser(SysUser oldSysUser, SysUser newSysUser, Integer year, Integer month, String type, String oldDeptName, String newDeptName) {
        ChangeSheetUser changeSheetUser = new ChangeSheetUser();
        changeSheetUser.setYear(year)
                .setMonth(month)
                .setType(type.replaceAll("不在职之|离退休之", ""))
                .setName(newSysUser.getName())
                .setUserSort(oldSysUser.getSort())
                .setOldDept(oldDeptName)
                .setNewDept(newDeptName)
                .setReason(newSysUser.getComment())
                .setStartDept(oldDeptName)
                .setSysUserId(oldSysUser.getId());
        return changeSheetUser;
    }

    //联动修改sal_np表中的当月的用户的信息
    private boolean updateSalNpUserInfo(SysUser oldSysUser, SysUser newSysUser) {
        SalNp salNp = salNpService.getOne(new QueryWrapper<SalNp>().eq("user_name", oldSysUser.getName()).eq("finish", Constants.FINISH_STATUS_NO));
        if (salNp == null) return true;
        salNp.setUserId(newSysUser.getId());
        salNp.setUserName(newSysUser.getName());
        salNp.setUserCategory(newSysUser.getCategory());
        salNp.setUserBankAccount(newSysUser.getBankAccount());
        salNp.setUserStatus(newSysUser.getStatus());
        salNp.setUserJob(newSysUser.getJob());
        salNp.setUserGiveMode(newSysUser.getGiveMode());
        salNp.setUserSort(newSysUser.getSort());
        salNp.setUserDeptId(newSysUser.getDeptId());
        salNp.setUserDeptName(newSysUser.getDeptName());
        salNp.setUserOrg(newSysUser.getOrg());
        return salNpService.updateById(salNp);
    }

    //联动修改sal_ltx表中的当月的用户的信息
    private boolean updateSalLtxUserInfo(SysUser oldSysUser, SysUser newSysUser) {
        SalLtx salLtx = salLtxService.getOne(new QueryWrapper<SalLtx>().eq("user_name", oldSysUser.getName()).eq("finish", Constants.FINISH_STATUS_NO));
        if (salLtx == null) return true;
        salLtx.setUserId(newSysUser.getId());
        salLtx.setUserName(newSysUser.getName());
        salLtx.setUserCategory(newSysUser.getCategory());
        salLtx.setUserBankAccount(newSysUser.getBankAccount());
        salLtx.setUserStatus(newSysUser.getStatus());
        salLtx.setUserJob(newSysUser.getJob());
        salLtx.setUserGiveMode(newSysUser.getGiveMode());
        salLtx.setUserSort(newSysUser.getSort());
        salLtx.setUserDeptId(newSysUser.getDeptId());
        salLtx.setUserDeptName(newSysUser.getDeptName());
        salLtx.setUserOrg(newSysUser.getOrg());
        return salLtxService.updateById(salLtx);
    }

    //联动修改sal_tx表中的当月的用户的信息
    private boolean updateSalLxUserInfo(SysUser oldSysUser, SysUser newSysUser) {
        SalLx salLx = salLxService.getOne(new QueryWrapper<SalLx>().eq("user_name", oldSysUser.getName()).eq("finish", Constants.FINISH_STATUS_NO));
        if (salLx == null) return true;
        salLx.setUserId(newSysUser.getId());
        salLx.setUserName(newSysUser.getName());
        salLx.setUserCategory(newSysUser.getCategory());
        salLx.setUserBankAccount(newSysUser.getBankAccount());
        salLx.setUserStatus(newSysUser.getStatus());
        salLx.setUserJob(newSysUser.getJob());
        salLx.setUserGiveMode(newSysUser.getGiveMode());
        salLx.setUserSort(newSysUser.getSort());
        salLx.setUserDeptId(newSysUser.getDeptId());
        salLx.setUserDeptName(newSysUser.getDeptName());
        salLx.setUserOrg(newSysUser.getOrg());
        return salLxService.updateById(salLx);
    }
}
