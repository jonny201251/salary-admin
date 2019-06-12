package com.hthyaq.salaryadmin.service;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface WorkFlowService {
    //部署流程
    Deployment deploy();
    //级联删除流程部署
    void deleteDeploy(String deployId);
    //根据业务id,启动流程实例
    ProcessInstance startProcess(String id);
    //根据流程实例id，获取userTask
    Task getRunTaskByProcessInstanceId(String processInstanceId);
    //根据业务id，获取userTask
    Task getRunTaskByBusinessId(String businessId);
    //根据业务id，获取processInstanceId
    String getProcessInstanceIdByBusinessId(String businessId);
    //根据业务id，判断流程是否结束
    boolean isFinishByBusinessId(String businessId);
    //根据流程实例id,判断流程是否结束
    boolean isFinishByProcessInstanceId(String processInstanceId);
    //完成任务
    void completeTask(String userName,String buttonName,Task task);
    //完成任务后，获取下一个用户任务的名称
    String getNextTaskName(String processInstanceId);
    //获取当前任务节点的坐标
    Map<String,Double> getCoordinate(String processInstanceId);
    //根据用户获取业务ids
    List<Integer> getBusinessIdsByUserName(String userName);
    //根据流程实例id,查询出业务id
    Integer getBusinessIdByProcessInstanceId(String processInstanceId);
}
