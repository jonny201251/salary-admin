package com.hthyaq.salaryadmin.controller;

import com.alibaba.excel.metadata.BaseRowModel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusHeader;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeNameComment;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusIncludeUserName;
import com.hthyaq.salaryadmin.bo.otherBonus.OtherBonusModel;
import com.hthyaq.salaryadmin.entity.ChangeSheetAttachment;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.ReflectUtil;
import com.hthyaq.salaryadmin.util.dateCache.DateCacheUtil;
import com.hthyaq.salaryadmin.util.dateCache.NoFinishSalaryDate;
import com.hthyaq.salaryadmin.util.excel.ExcelUtil;
import com.hthyaq.salaryadmin.util.excel.ResponseExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;

//导出excel
@Api
@Controller
@RequestMapping("/changeSheetFile")
public class ChangeSheetFileController {
    @Autowired
    ChangeSheetAttachmentService changeSheetAttachmentService;
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalLtxService salLtxService;
    @Autowired
    SalLxService salLxService;
    @Autowired
    ChangeSheetService changeSheetService;

    @ApiOperation("批量修改-内聘+退休+离休")
    @GetMapping("/salaryFile")
    public void salaryFile(Integer id, HttpServletResponse response) throws Exception {
        ChangeSheetAttachment changeSheetAttachment = changeSheetAttachmentService.getById(id);
        new ResponseExcel().download(changeSheetAttachment.getPath(), response);
    }

    @ApiOperation("其他薪金")
    @GetMapping("/otherBonus")
    public void otherBonus(HttpServletResponse response) throws Exception {
        //1.分别获取 内聘+退休+离休-其他薪金的数据
        List<OtherBonusIncludeUserName> dataList1 = changeSheetService.getOtherBonusIncludeUserName(Constants.SAL_NP);
        List<OtherBonusIncludeUserName> dataList2 = changeSheetService.getOtherBonusIncludeUserName(Constants.SAL_LTX);
        List<OtherBonusIncludeUserName> dataList3 = changeSheetService.getOtherBonusIncludeUserName(Constants.SAL_LX);

        //2.分别获取 内聘+退休+离休-其他薪金的名称
        List<OtherBonusIncludeNameComment> headerList1 = changeSheetService.getOtherBonusIncludeNameComment(Constants.SAL_NP);
        List<OtherBonusIncludeNameComment> headerList2 = changeSheetService.getOtherBonusIncludeNameComment(Constants.SAL_LTX);
        List<OtherBonusIncludeNameComment> headerList3 = changeSheetService.getOtherBonusIncludeNameComment(Constants.SAL_LX);

        Map<String, OtherBonusHeader> headerMap1 = getOtherBonusHeader(headerList1);
        Map<String, OtherBonusHeader> headerMap2 = getOtherBonusHeader(headerList2);
        Map<String, OtherBonusHeader> headerMap3 = getOtherBonusHeader(headerList3);

        //3.拼接数据
        List<OtherBonusModel> salNpData = getOtherBonusModel(dataList1, headerMap1);
        List<OtherBonusModel> salLtxData = getOtherBonusModel(dataList2, headerMap2);
        List<OtherBonusModel> salLxData = getOtherBonusModel(dataList3, headerMap3);

        Map<String, List<? extends BaseRowModel>> dataMap = Maps.newTreeMap();
        if (CollectionUtil.isNotNullOrEmpty(salNpData)) {
            dataMap.put("内聘", salNpData);
        }
        if (CollectionUtil.isNotNullOrEmpty(salLtxData)) {
            dataMap.put("退休", salLtxData);
        }
        if (CollectionUtil.isNotNullOrEmpty(salLxData)) {
            dataMap.put("离休", salLxData);
        }

        //获取工资表中的未月结的年份和月份
        NoFinishSalaryDate noFinishSalaryDate = DateCacheUtil.get(Constants.SAL_NP);
        String file = Constants.TMP_PAHT + Constants.FILE_SEPARATOR + noFinishSalaryDate.getYear() + "年" + noFinishSalaryDate.getMonth() + "月-" + "其他薪金明细" + ".xlsx";
        //先删除
        File fileTmp = new File(file);
        if (fileTmp.exists()) {
            fileTmp.delete();
        }

        ExcelUtil.generateExcelMoreSheet(file, dataMap, OtherBonusModel.class, false);
        new ResponseExcel().download(file, response);
    }

    private Map<String, OtherBonusHeader> getOtherBonusHeader(List<OtherBonusIncludeNameComment> headerList) {
        /*
         将一下数据处理成只保留一行
         name           comment
        考核优秀奖励
        考核优秀奖励	xx的备注---
        考核优秀奖励	yy的备注--
         */
        Map<String, OtherBonusIncludeNameComment> preMap = Maps.newLinkedHashMap();
        for (OtherBonusIncludeNameComment tmp : headerList) {
            OtherBonusIncludeNameComment tmp2 = preMap.get(tmp.getName());
            if (tmp2 == null) {
                preMap.put(tmp.getName(), tmp);
            } else {
                String comment = tmp2.getComment() + tmp.getComment();
                tmp2.setComment(comment);
            }
        }
        List<OtherBonusIncludeNameComment> preHeaderList = Lists.newArrayList(preMap.values());
        //
        Map<String, OtherBonusHeader> map = Maps.newLinkedHashMap();
        //记录OtherBonusModel中的var的后面的数字
        int index = 2;
        if (CollectionUtil.isNotNullOrEmpty(preHeaderList)) {
            for (OtherBonusIncludeNameComment tmp : preHeaderList) {
                String name = tmp.getName();
                if (!Strings.isNullOrEmpty(name)) {
                    OtherBonusHeader otherBonusHeader = new OtherBonusHeader(name,index,tmp.getComment());
                    if (Strings.isNullOrEmpty(tmp.getComment())) {
                        index = index + 1;
                    } else {
                        index = index + 2;
                    }
                    map.put(name, otherBonusHeader);
                }
            }
        }
        return map;
    }

    private List<OtherBonusModel> getOtherBonusModel(List<OtherBonusIncludeUserName> dataList, Map<String, OtherBonusHeader> headerMap) {
        List<OtherBonusModel> list = Lists.newArrayList();
        if (CollectionUtil.isNullOrEmpty(dataList)) {
            return list;
        }
        //表头
        OtherBonusModel header = new OtherBonusModel();
        header.setUserDeptName("部门");
        header.setUserName("姓名");
        ReflectUtil reflectUtil = new ReflectUtil();
        for (Map.Entry<String, OtherBonusHeader> entry : headerMap.entrySet()) {
            OtherBonusHeader tmp = entry.getValue();
            reflectUtil.invoke(header, "setVar" + tmp.getIndex(), tmp.getName());
            if (!Strings.isNullOrEmpty(tmp.getComment())) {
                reflectUtil.invoke(header, "setVar" + (tmp.getIndex() + 1), "备注");
            }
        }
        list.add(header);
        //数据
        Map<String, OtherBonusModel> map = Maps.newLinkedHashMap();
        for (OtherBonusIncludeUserName tmp : dataList) {
            String userName = tmp.getUserName();
            OtherBonusModel data = map.get(userName);
            if (data == null) {
                data = new OtherBonusModel();
                data.setUserDeptName(tmp.getUserDeptName());
                data.setUserName(userName);
                // 放入map
                map.put(userName, data);
            }
            //取出薪金的名称对应的index
            int index = headerMap.get(tmp.getName()).getIndex();
            reflectUtil.invoke(data, "setVar" + index, tmp.getMoney() + "");
            if (!Strings.isNullOrEmpty(tmp.getComment())) {
                reflectUtil.invoke(data, "setVar" + (index + 1), tmp.getComment() + "");
            }
        }
        //map转list
        list.addAll(map.values());
        return list;
    }

}
