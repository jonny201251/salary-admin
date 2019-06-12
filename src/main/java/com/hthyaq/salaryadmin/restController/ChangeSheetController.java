package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.entity.ChangeSheet;
import com.hthyaq.salaryadmin.entity.ChangeSheetAttachment;
import com.hthyaq.salaryadmin.service.ChangeSheetAnnotationService;
import com.hthyaq.salaryadmin.service.ChangeSheetAttachmentService;
import com.hthyaq.salaryadmin.service.ChangeSheetService;
import com.hthyaq.salaryadmin.service.WorkFlowService;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.YearMonth;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.vo.ChangeSheeTaskAndtAnnotation;
import com.hthyaq.salaryadmin.vo.ChangeSheetPageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 变动单 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Api
@RestController
@RequestMapping("/changeSheet")
public class ChangeSheetController {
    @Autowired
    ChangeSheetService changeSheetService;
    @Autowired
    ChangeSheetAnnotationService changeSheetAnnotationService;
    @Autowired
    ChangeSheetAttachmentService changeSheetAttachmentService;
    @Autowired
    WorkFlowService workFlowService;

    @ApiOperation("部署-变动单")
    @GetMapping("/deploy")
    public Deployment deploy() {
        return workFlowService.deploy();
    }

    @ApiOperation("根据分页查询出-变动单")
    @GetMapping("/list")
    public Page<ChangeSheet> list(Integer pageNum, String flag, String userName, String yearmonth) {
        Page<ChangeSheet> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        QueryWrapper<ChangeSheet> queryWrapper = new QueryWrapper<ChangeSheet>().orderByDesc("year", "month", "create_time");
        if ("ChangeSheetNeedHandle".equals(flag)) {
            //流程没有结束
            queryWrapper.ne("process_status", Constants.PROCESS_END);
            //查询出属于用户的流程
            List<Integer> ids = workFlowService.getBusinessIdsByUserName(userName);
            if (!CollectionUtil.isNotNullOrEmpty(ids)) {
                //为了sql能够拼接出in(0)，如果在使用in方法，值为空时，会省略in
                ids.add(0);
            }
            queryWrapper.in("id", ids);
        }
        if (!Strings.isNullOrEmpty(yearmonth)) {
            Map<String, Integer> map = YearMonth.getStartEndYearMonth(yearmonth);
            queryWrapper.between("yearmonth_int", map.get("start"), map.get("end"));
        }
        changeSheetService.page(pagination, queryWrapper);
        return pagination;
    }

    @ApiOperation("人事专员和财务专员的提交和修改-变动单")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateChangeSheet(@RequestBody ChangeSheetPageData changeSheetPageData) {
        String type = changeSheetPageData.getType();
        if (Constants.BUTTON_TYPE_VALIDATE.equals(type) || Constants.BUTTON_TYPE_CONFIRM.equals(type) || Constants.BUTTON_TYPE_AUDITING.equals(type)) {
            //完成用户任务和保存领导批注
            changeSheetService.completeUserTaskAndSaveLeaderAnnotation(changeSheetPageData);
            return true;
        }
        //create_time
        changeSheetPageData.setCreateTime(LocalDateTime.now());
        return changeSheetService.saveOrUpdateComplexData(changeSheetPageData);
    }

    @ApiOperation("领导的批注-变动单")
    @GetMapping("/annotation")
    public List<ChangeSheeTaskAndtAnnotation> annotationChangeSheet(String changeSheetId) {
        return changeSheetAnnotationService.getAnnotationByChangeSheetId(changeSheetId);
    }

    @ApiOperation("编辑按钮的反显数据-变动单")
    @GetMapping("/editView")
    public ChangeSheetPageData editView(Long changeSheetId) {
        return changeSheetService.editViewComplexData(changeSheetId);
    }

    @ApiOperation("查看流程图-变动单")
    @GetMapping("/viewDiagram")
    public Map<String, Double> viewDiagram(String changeSheetId) {
        boolean isFinish = workFlowService.isFinishByBusinessId(changeSheetId);
        if (isFinish) {
            return Maps.newHashMap();
        }
        String processInstanceId = workFlowService.getProcessInstanceIdByBusinessId(changeSheetId);
        Map<String, Double> coordinate = workFlowService.getCoordinate(processInstanceId);
        return coordinate;
    }

    @ApiOperation("查看审核过程-变动单")
    @GetMapping("/viewStep")
    public List<ChangeSheeTaskAndtAnnotation> viewStep(String changeSheetId) {
        return changeSheetAnnotationService.getAnnotationByChangeSheetId(changeSheetId);
    }

    @ApiOperation("批量修改记录-变动单")
    @GetMapping("/batchModifyData")
    public List<ChangeSheetAttachment> batchModifyData() {
        List<ChangeSheetAttachment> result = Lists.newArrayList();
        ChangeSheetAttachment salNpAttachment = null, salLtxAttachment = null, salLxAttachment = null;
        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        List<ChangeSheetAttachment> list = changeSheetAttachmentService.list(new QueryWrapper<ChangeSheetAttachment>().eq("year", noFinishSalaryDate.getYear()).eq("month", noFinishSalaryDate.getMonth()).orderByDesc("operate_time"));
        for (ChangeSheetAttachment tmp : list) {
            if (Constants.SAL_NP.equals(tmp.getTName()) && salNpAttachment == null) {
                salNpAttachment = tmp;
            } else if (Constants.SAL_LTX.equals(tmp.getTName()) && salLtxAttachment == null) {
                salLtxAttachment = tmp;
            } else if (Constants.SAL_LX.equals(tmp.getTName()) && salLxAttachment == null) {
                salLxAttachment = tmp;
            }
        }
        if (salNpAttachment != null) {
            result.add(salNpAttachment);
        }
        if (salLtxAttachment != null) {
            result.add(salLtxAttachment);
        }
        if (salLxAttachment != null) {
            result.add(salLxAttachment);
        }
        return result;
    }

    @ApiOperation("删除部署-测试使用")
    @GetMapping("/delete")
    public void delete(String id) {
        workFlowService.deleteDeploy(id);
    }
}
