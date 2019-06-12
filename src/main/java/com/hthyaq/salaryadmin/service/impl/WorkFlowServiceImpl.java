package com.hthyaq.salaryadmin.service.impl;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.service.SysRoleUserService;
import com.hthyaq.salaryadmin.service.WorkFlowService;
import com.hthyaq.salaryadmin.vo.RoleAndUserName;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    TaskService taskService;
    @Autowired
    HistoryService historyService;
    @Autowired
    SysRoleUserService sysRoleUserService;

    //部署流程
    @Override
    public Deployment deploy() {
        return repositoryService.createDeployment()
                .name("changeSheet")
                .addClasspathResource("processes/changeSheet.bpmn")
                .addClasspathResource("processes/changeSheet.png")
                .deploy();
    }

    //级联删除流程部署
    @Override
    public void deleteDeploy(String deployId) {
        repositoryService.deleteDeployment(deployId, true);
    }

    //根据业务id,启动流程实例
    @Override
    public ProcessInstance startProcess(String changeSheetId) {
        //是否部署流程
        Deployment deployment = repositoryService.createDeploymentQuery().singleResult();
        if (null == deployment) {
            this.deploy();
        }
        String processDefinitionKey = "changeSheet";
        String businessKey = "change_sheet." + changeSheetId;
        //绑定处理人
        Map<String, Object> variables = bindCandidateUser();
        return runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    //根据流程实例id，获取userTask
    @Override
    public Task getRunTaskByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    }

    //根据业务id，获取userTask
    @Override
    public Task getRunTaskByBusinessId(String businessId) {
        HistoricProcessInstance hpi = this.getHistoricProcessInstance(businessId);
        return getRunTaskByProcessInstanceId(hpi.getId());
    }

    //根据业务id，获取processInstanceId
    @Override
    public String getProcessInstanceIdByBusinessId(String businessId) {
        HistoricProcessInstance hpi = this.getHistoricProcessInstance(businessId);
        return hpi.getId();
    }

    //根据业务id,判断流程是否结束
    @Override
    public boolean isFinishByBusinessId(String businessId) {
        String processInstanceId = this.getProcessInstanceIdByBusinessId(businessId);
        return this.isFinishByProcessInstanceId(processInstanceId);
    }

    //根据流程实例id,判断流程是否结束
    @Override
    public boolean isFinishByProcessInstanceId(String processInstanceId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (pi == null) return true;
        return false;
    }

    //根据业务id,获取历史流程实例
    private HistoricProcessInstance getHistoricProcessInstance(String businessId) {
        String businessKey = "change_sheet." + businessId;
        return historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
    }

    //完成任务
    public void completeTask(String userName, String buttonName, Task task) {
        String taskId = task.getId();
        //拾取任务
        taskService.claim(taskId, userName);
        //绑定用户任务的操作按钮
        HashMap<String, Object> button = getButton(task.getName(), buttonName);
        //完成任务
        taskService.complete(taskId, button);
    }

    //完成任务后，获取下一个用户任务的名称
    @Override
    public String getNextTaskName(String processInstanceId) {
        boolean isFinish = this.isFinishByProcessInstanceId(processInstanceId);
        if (isFinish) return Constants.PROCESS_END;
        Task task = this.getRunTaskByProcessInstanceId(processInstanceId);
        return task.getName();
    }

    private HashMap<String, Object> getButton(String taskName, String buttonName) {
        HashMap<String, Object> button = Maps.newHashMap();
        String buttonKey = "";
        if ("人事专员-生成变动单".equals(taskName)) {
            buttonKey = "humanResourceGenerateButton";
        } else if ("财务专员-校对".equals(taskName)) {
            buttonKey = "financeValidateButton";
        } else if ("人事专员-确认".equals(taskName)) {
            buttonKey = "humanResourceConfirmButton";
        } else if ("人事领导-审批".equals(taskName)) {
            buttonKey = "humanResourceLeaderButton";
        } else if ("财务领导-审批".equals(taskName)) {
            buttonKey = "financeLeaderButton";
        } else if ("公司人事主管领导-审批".equals(taskName)) {
            buttonKey = "companyLeaderButton";
        }
        button.put(buttonKey, buttonName);
        return button;
    }

    //获取当前任务节点的坐标
    public Map<String, Double> getCoordinate(String processInstanceId) {
        HashMap<String, Double> coordinates = Maps.newHashMap();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType("userTask")
                .unfinished()
                .orderByHistoricActivityInstanceEndTime().asc().list();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(list.get(list.size() - 1).getActivityId());
        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(currentFlowNode.getId());
        coordinates.put("x", graphicInfo.getX());
        coordinates.put("y", graphicInfo.getY());
        coordinates.put("width", graphicInfo.getWidth());
        coordinates.put("height", graphicInfo.getHeight());
        return coordinates;
    }

    @Override
    public List<Integer> getBusinessIdsByUserName(String userName) {
        List<Integer> ids = Lists.newArrayList();
        //根据userName，查询出组任务
        List<Task> list = taskService.createTaskQuery().taskCandidateUser(userName).list();
        if (CollectionUtil.isNotNullOrEmpty(list)) {
            list.forEach(task -> ids.add(this.getBusinessIdByProcessInstanceId(task.getProcessInstanceId())));
        }
        return ids;
    }

    //根据流程实例id,查询出业务id
    @Override
    public Integer getBusinessIdByProcessInstanceId(String processInstanceId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //格式为"change_sheet.id"
        String buniness_key = pi.getBusinessKey();
        String id = Splitter.on(".").splitToList(buniness_key).get(1);
        return Integer.parseInt(id);
    }

    //流程启动时，给所有任务绑定处理人
    private Map<String, Object> bindCandidateUser() {
        Map<String, Object> candidateUsers = Maps.newHashMap();
        List<RoleAndUserName> processUserNames = sysRoleUserService.getProcessUserNames();
        Multimap<String, String> multimap = ArrayListMultimap.create();
        processUserNames.forEach(tmp -> multimap.put(tmp.getRoleName(), tmp.getUserName()));
        //人事专员-生成变动单,#{humanResourceGenerateNames}
        candidateUsers.put("humanResourceGenerateNames", Joiner.on(",").join(multimap.get("人事专员")));
        //财务专员-校对,#{financeValidateNames}
        candidateUsers.put("financeValidateNames", Joiner.on(",").join(multimap.get("财务专员")));
        //人事专员-确认,#{humanResourceConfirmNames}
        candidateUsers.put("humanResourceConfirmNames", Joiner.on(",").join(multimap.get("人事确认专员")));
        //人事领导-审批,#{humanResourceLeaderNames}
        candidateUsers.put("humanResourceLeaderNames", Joiner.on(",").join(multimap.get("人事领导")));
        //财务领导-审批,#{financeLeaderNames}
        candidateUsers.put("financeLeaderNames", Joiner.on(",").join(multimap.get("财务领导")));
        //公司人事主管领导-审批,#{companyLeaderNames}
        candidateUsers.put("companyLeaderNames", Joiner.on(",").join(multimap.get("公司人事主管领导")));
        return candidateUsers;
    }
}
