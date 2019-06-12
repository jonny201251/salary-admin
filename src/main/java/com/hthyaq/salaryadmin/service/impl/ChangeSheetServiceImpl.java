package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeNameComment;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeUserName;
import com.hthyaq.salaryadmin.entity.*;
import com.hthyaq.salaryadmin.mapper.ChangeSheetMapper;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.YearMonth;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.vo.ChangeSheetPageData;
import com.hthyaq.salaryadmin.vo.Repeater;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变动单 服务实现类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Service
public class ChangeSheetServiceImpl extends ServiceImpl<ChangeSheetMapper, ChangeSheet> implements ChangeSheetService {
    @Autowired
    ChangeSheetUserService changeSheetUserService;
    @Autowired
    ChangeSheetDeptService changeSheetDeptService;
    @Autowired
    ChangeSheetSalService changeSheetSalService;
    @Autowired
    ChangeSheetBonusService changeSheetBonusService;
    @Autowired
    ChangeSheetAnnotationService changeSheetAnnotationService;
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalLtxService salLtxService;
    @Autowired
    SalLxService salLxService;

    @Autowired
    private WorkFlowService workFlowService;


    //保存变动单，并启动流程实例
    @Override
    public boolean saveOrUpdateComplexData(ChangeSheetPageData changeSheetPageData) {
        boolean flag = true, flag2 = true;
        Long startChangeSheetId = changeSheetPageData.getId();
        //根据yearmonth_string抽取出year、month、yearmonth_int
        Map<String, Integer> yearmonth = YearMonth.getYearMonth(changeSheetPageData.getYearmonthString());
        changeSheetPageData.setYear(yearmonth.get("year"));
        changeSheetPageData.setMonth(yearmonth.get("month"));
        changeSheetPageData.setYearmonthInt(yearmonth.get("yearMonthInt"));
        //保存-变动单
        ChangeSheet changeSheet = new ChangeSheet();
        BeanUtils.copyProperties(changeSheetPageData, changeSheet);
        flag = this.saveOrUpdate(changeSheet);
        Long changeSheetId = changeSheet.getId();
        if (flag) {
            flag2 = changeSheetDetail(changeSheetId, startChangeSheetId, changeSheetPageData);
        }
        if (flag && flag2) {
            if (Constants.BUTTON_TYPE_GENERATE.equals(changeSheetPageData.getType()) || Constants.COMMIT_TO_HUMAN_LEADER.equals(changeSheetPageData.getType())) {
                //启动流程
                ProcessInstance processInstance = workFlowService.startProcess(changeSheetId + "");
                String processInstanceId = processInstance.getId();
                //先绑定业务id,再自动完成第一个用户任务的提交
                changeSheetPageData.setId(changeSheetId);
                workFlowService.getRunTaskByProcessInstanceId(processInstanceId);
                this.completeUserTaskAndSaveLeaderAnnotation(changeSheetPageData);
                String processStatus = workFlowService.getNextTaskName(processInstanceId);
                //更新-变动单的流程状态
                changeSheet.setProcessStatus(processStatus);
                flag2 = flag2 && this.saveOrUpdate(changeSheet);
            } else if (Constants.BUTTON_TYPE_BACK.equals(changeSheetPageData.getType())) {
                //完成用户任务和保存领导批注
                this.completeUserTaskAndSaveLeaderAnnotation(changeSheetPageData);
            }
        }
        return flag && flag2;
    }

    @Override
    public ChangeSheetPageData editViewComplexData(Long changeSheetId) {
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        Integer year = noFinishSalaryDate.getYear();
        Integer month = noFinishSalaryDate.getMonth();
        ChangeSheetPageData changeSheetPageData = new ChangeSheetPageData();
        //生成按钮
        if (changeSheetId == null) {
            changeSheetPageData.setYear(year);
            changeSheetPageData.setMonth(month);
            changeSheetPageData.setYearmonthInt(noFinishSalaryDate.getYearmonthInt());
            changeSheetPageData.setYearmonthString(noFinishSalaryDate.getYearmonthString());
            //人员
            List<ChangeSheetUser> users = changeSheetUserService.list(new QueryWrapper<ChangeSheetUser>().eq("year", year).eq("month", month).eq("change_sheet_id", 0).orderByAsc("user_sort"));
            changeSheetPageData.setUser_repeater(new Repeater<>(users));
            //部门
            List<ChangeSheetDept> depts = changeSheetDeptService.list(new QueryWrapper<ChangeSheetDept>().eq("year", year).eq("month", month).eq("change_sheet_id", 0));
            changeSheetPageData.setDept_repeater(new Repeater<>(depts));
            //内聘工资
            List<ChangeSheetSal> salNps = changeSheetSalService.list(new QueryWrapper<ChangeSheetSal>().eq("year", year).eq("month", month).eq("t_name", Constants.SAL_NP).eq("change_sheet_id", 0).orderByAsc("user_sort"));
            changeSheetPageData.setSalNp_repeater(new Repeater<>(salNps));
            //退休+离休工资
            List<ChangeSheetSal> salLtxs = changeSheetSalService.list(new QueryWrapper<ChangeSheetSal>().eq("year", year).eq("month", month).in("t_name", Constants.SAL_LTX, Constants.SAL_LX).eq("change_sheet_id", 0).orderByAsc("user_sort"));
            changeSheetPageData.setSalTx_repeater(new Repeater<>(salLtxs));
            //内聘+退休+离休-其他薪金
            List<SalNp> salNpList = salNpService.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO));
            List<SalLtx> salLtxList = salLtxService.list(new QueryWrapper<SalLtx>().eq("finish", Constants.FINISH_STATUS_NO));
            List<SalLx> salLxList = salLxService.list(new QueryWrapper<SalLx>().eq("finish", Constants.FINISH_STATUS_NO));
            List<Long> ids = Lists.newArrayList();
            salNpList.forEach(tmp -> ids.add(tmp.getId()));
            salLtxList.forEach(tmp -> ids.add(tmp.getId()));
            salLxList.forEach(tmp -> ids.add(tmp.getId()));
            List<SalBonus> salBonuss = salBonusService.list(new QueryWrapper<SalBonus>().in("sal_id", ids));
            Multimap<String, SalBonus> multimap = ArrayListMultimap.create();
            salBonuss.forEach(salBonus -> multimap.put(salBonus.getName(), salBonus));
            List<ChangeSheetBonus> changeSheetBonuss = Lists.newArrayList();
            if (CollectionUtil.isNotNullOrEmpty(multimap.entries())) {
                multimap.asMap().forEach((k, v) -> {
                    ChangeSheetBonus changeSheetBonus = new ChangeSheetBonus();
                    changeSheetBonus.setName(k);
                    changeSheetBonus.setYear(year);
                    changeSheetBonus.setMonth(month);
                    Double sum = v.stream().map(SalBonus::getMoney).reduce(0.0, Double::sum);
                    changeSheetBonus.setMoney(sum);
                    changeSheetBonuss.add(changeSheetBonus);
                });
            }
            changeSheetPageData.setJiangjin_repeater(new Repeater<>(changeSheetBonuss));
        } else {
            //编辑按钮
            ChangeSheet changeSheet = this.getById(changeSheetId);
            BeanUtils.copyProperties(changeSheet, changeSheetPageData);
            //人员
            List<ChangeSheetUser> users = changeSheetUserService.list(new QueryWrapper<ChangeSheetUser>().eq("year", year).eq("month", month).eq("change_sheet_id", changeSheetId).orderByAsc("user_sort"));
            changeSheetPageData.setUser_repeater(new Repeater<>(users));
            //部门
            List<ChangeSheetDept> depts = changeSheetDeptService.list(new QueryWrapper<ChangeSheetDept>().eq("year", year).eq("month", month).eq("change_sheet_id", changeSheetId));
            changeSheetPageData.setDept_repeater(new Repeater<>(depts));
            //内聘工资
            List<ChangeSheetSal> salNps = changeSheetSalService.list(new QueryWrapper<ChangeSheetSal>().eq("year", year).eq("month", month).eq("t_name", Constants.SAL_NP).eq("change_sheet_id", changeSheetId).orderByAsc("user_sort"));
            changeSheetPageData.setSalNp_repeater(new Repeater<>(salNps));
            //退休+离休的工资
            List<ChangeSheetSal> salTxs = changeSheetSalService.list(new QueryWrapper<ChangeSheetSal>().eq("year", year).eq("month", month).in("t_name", Constants.SAL_LTX, Constants.SAL_LX).eq("change_sheet_id", changeSheetId).orderByAsc("user_sort"));
            changeSheetPageData.setSalTx_repeater(new Repeater<>(salTxs));
            //其他薪金
            List<ChangeSheetBonus> bonuss = changeSheetBonusService.list(new QueryWrapper<ChangeSheetBonus>().eq("year", year).eq("month", month).eq("change_sheet_id", changeSheetId));
            changeSheetPageData.setJiangjin_repeater(new Repeater<>(bonuss));
        }
        return changeSheetPageData;
    }

    @Override
    public Task completeUserTask(String changeSheetId, String userName, String buttonName) {
        //根据changeSheetId查询出task
        Task task = workFlowService.getRunTaskByBusinessId(changeSheetId);
        //完成任务
        workFlowService.completeTask(userName, buttonName, task);
        //更新变动单的流程状态
        String taskName = workFlowService.getNextTaskName(task.getProcessInstanceId());
        this.updateProcessStatusById(changeSheetId, taskName);
        return task;
    }

    @Override
    public int updateProcessStatusById(String changeSheetId, String taskName) {
        return this.baseMapper.updateProcessStatusById(Integer.parseInt(changeSheetId), taskName);
    }

    @Override
    public void completeUserTaskAndSaveLeaderAnnotation(ChangeSheetPageData changeSheetPageData) {
        Long changeSheetId = changeSheetPageData.getId();
        //完成任务
        Task task = this.completeUserTask(changeSheetId + "", changeSheetPageData.getUserName(), changeSheetPageData.getButtonName());
        //保存领导批注
        ChangeSheetAnnotation changeSheetAnnotation = new ChangeSheetAnnotation();
        changeSheetAnnotation.setAnnotation(changeSheetPageData.getAnnotation());
        changeSheetAnnotation.setChangeSheetId(changeSheetId);
        changeSheetAnnotation.setTaskId(task.getId());
        changeSheetAnnotation.setButtonName(changeSheetPageData.getButtonName());
        changeSheetAnnotationService.save(changeSheetAnnotation);
    }

    @Override
    public List<OtherBonusIncludeNameComment> getOtherBonusIncludeNameComment(String tName) {
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(tName);
        if (Constants.SAL_NP.equals(tName)) {
            return this.baseMapper.getOtherBonusIncludeNameCommentBySalNp(noFinishSalaryDate.getYear(),noFinishSalaryDate.getMonth());
        } else if (Constants.SAL_LTX.equals(tName)) {
            return this.baseMapper.getOtherBonusIncludeNameCommentBySalLtx(noFinishSalaryDate.getYear(),noFinishSalaryDate.getMonth());
        } else {
            return this.baseMapper.getOtherBonusIncludeNameCommentBySalLx(noFinishSalaryDate.getYear(),noFinishSalaryDate.getMonth());
        }
    }

    @Override
    public List<OtherBonusIncludeUserName> getOtherBonusIncludeUserName(String tName) {
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(tName);
        if (Constants.SAL_NP.equals(tName)) {
            return this.baseMapper.getOtherBonusIncludeUserNameBySalNp(noFinishSalaryDate.getYear(),noFinishSalaryDate.getMonth());
        } else if (Constants.SAL_LTX.equals(tName)) {
            return this.baseMapper.getOtherBonusIncludeUserNameBySalLtx(noFinishSalaryDate.getYear(),noFinishSalaryDate.getMonth());
        } else {
            return this.baseMapper.getOtherBonusIncludeUserNameBySalLx(noFinishSalaryDate.getYear(),noFinishSalaryDate.getMonth());
        }
    }

    private boolean changeSheetDetail(Long changeSheetId, Long startChangeSheetId, ChangeSheetPageData changeSheetPageData) {
        boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true;
        if (startChangeSheetId != null) {
            //先删除
            changeSheetUserService.remove(new QueryWrapper<ChangeSheetUser>().eq("change_sheet_id", changeSheetId));
            changeSheetDeptService.remove(new QueryWrapper<ChangeSheetDept>().eq("change_sheet_id", changeSheetId));
            //工资变动+退休
            changeSheetSalService.remove(new QueryWrapper<ChangeSheetSal>().eq("change_sheet_id", changeSheetId));
            changeSheetBonusService.remove(new QueryWrapper<ChangeSheetBonus>().eq("change_sheet_id", changeSheetId));
        }
        //用户变动
        List<ChangeSheetUser> users = changeSheetPageData.getUser_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(users)) {
            users.forEach(user -> user.setChangeSheetId(changeSheetId));
            flag1 = changeSheetUserService.saveBatch(users);
        }
        //部门变动
        List<ChangeSheetDept> depts = changeSheetPageData.getDept_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(depts)) {
            depts.forEach(dept -> dept.setChangeSheetId(changeSheetId));
            flag2 = changeSheetDeptService.saveBatch(depts);
        }
        //工资变动
        List<ChangeSheetSal> salNps = changeSheetPageData.getSalNp_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(salNps)) {
            salNps.forEach(salNp -> salNp.setChangeSheetId(changeSheetId));
            flag3 = changeSheetSalService.saveBatch(salNps);
        }
        //退休
        List<ChangeSheetSal> salTxs = changeSheetPageData.getSalTx_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(salTxs)) {
            salTxs.forEach(salTx -> salTx.setChangeSheetId(changeSheetId));
            flag4 = changeSheetSalService.saveBatch(salTxs);
        }
        //奖金或过节费变动
        List<ChangeSheetBonus> bonuss = changeSheetPageData.getJiangjin_repeater().getDataSource();
        if (CollectionUtil.isNotNullOrEmpty(bonuss)) {
            bonuss.forEach(bonus -> bonus.setChangeSheetId(changeSheetId));
            flag5 = changeSheetBonusService.saveBatch(bonuss);
        }
        return flag1 && flag2 && flag3 && flag4 && flag5;
    }
}
