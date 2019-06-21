package com.hthyaq.salaryadmin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hthyaq.salaryadmin.bo.ChildrenTableBatchModify;
import com.hthyaq.salaryadmin.bo.YingfaYingkouSalLtx;
import com.hthyaq.salaryadmin.bo.YingfaYingkouSalLx;
import com.hthyaq.salaryadmin.bo.YingfaYingkouSalNp;
import com.hthyaq.salaryadmin.entity.*;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.*;
import com.hthyaq.salaryadmin.util.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//以后有时间，将该部分代码进行重写，太乱了.....
@Service
@Slf4j
public class UploadFileBatchModifyServiceImpl implements UploadFileBatchModifyService {
    @Autowired
    ChangeSheetAttachmentService changeSheetAttachmentService;
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalLtxService salLtxService;
    @Autowired
    SalLxService salLxService;
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SysDicService sysDicService;
    @Autowired
    SalNpTaxService salNpTaxService;

    @Override
    public boolean completeUpload(MultipartFile[] files, String type, String path, String comment) {
        boolean flag = false;
        if (Constants.PATH_SAL_NP.equals(path) && Constants.BATCH_MODIFY_YINGFA.equals(type)) {//内聘的应发应扣
            //遍历出excel的数据
            List<Object> excelDatas = iterateExcel(files, YingfaYingkouSalNp.class);
            Object obj = (Object) excelDatas;
            List<YingfaYingkouSalNp> yingfaYingkouSalNps = (List<YingfaYingkouSalNp>) obj;
            HashMap<String, YingfaYingkouSalNp> excelHashMap = Maps.newHashMap();
            //根据姓名从db中取出该人的工资数据
            List<String> excleUserNameList = Lists.newArrayList();
            yingfaYingkouSalNps.stream().forEach(yingfaYingkouSalNp -> {
                String userName = yingfaYingkouSalNp.getName();
                if (!Strings.isNullOrEmpty(userName)) {
                    excleUserNameList.add(userName);
                }
                excelHashMap.put(userName, yingfaYingkouSalNp);
            });
            List<SalNp> dbDatas = salNpService.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO).in("user_name", excleUserNameList));
            List<String> dbUserNameList = Lists.newArrayList();
            dbDatas.forEach(tmp -> dbUserNameList.add(tmp.getUserName()));
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            //将excel中的数据合并到db中
            ReflectUtil reflectUtil = new ReflectUtil();
            dbDatas.forEach(salNp -> {
                CoverObj.cover(reflectUtil, excelHashMap.get(salNp.getUserName()), salNp, "setDept", "setName");
            });
            //重新计算db中的税款、应发合计、应扣合计、实发合计
            dbDatas.forEach(salNp -> salNpService.onlyComputeNoTransactionForYingfa(salNp));
            //更新
            flag = salNpService.updateBatchById(dbDatas);
            if (flag) {
                //excel存储到硬盘和change_sheet_attachment表中
                storeExcel(files, comment, Constants.SAL_NP, dbDatas.get(0).getYear(), dbDatas.get(0).getMonth());
            }
        } else if (Constants.PATH_SAL_LTX.equals(path) && Constants.BATCH_MODIFY_YINGFA.equals(type)) { //退休的应发应扣
            //遍历出excel的数据
            List<Object> excelDatas = iterateExcel(files, YingfaYingkouSalLtx.class);
            Object obj = (Object) excelDatas;
            List<YingfaYingkouSalLtx> yingfaYingkouSalLtxs = (List<YingfaYingkouSalLtx>) obj;
            HashMap<String, YingfaYingkouSalLtx> excelHashMap = Maps.newHashMap();
            //根据姓名从db中取出该人的工资数据
            List<String> excleUserNameList = Lists.newArrayList();
            yingfaYingkouSalLtxs.stream().forEach(yingfaYingkouSalLtx -> {
                String userName = yingfaYingkouSalLtx.getName();
                if (userName != null) {
                    excleUserNameList.add(userName);
                }
                excelHashMap.put(userName, yingfaYingkouSalLtx);
            });
            List<SalLtx> dbDatas = salLtxService.list(new QueryWrapper<SalLtx>().eq("finish", Constants.FINISH_STATUS_NO).in("user_name", excleUserNameList));
            List<String> dbUserNameList = Lists.newArrayList();
            dbDatas.forEach(tmp -> dbUserNameList.add(tmp.getUserName()));
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            //将excel中的数据合并到db中
            ReflectUtil reflectUtil = new ReflectUtil();
            dbDatas.forEach(salLtx -> {
                CoverObj.cover(reflectUtil, excelHashMap.get(salLtx.getUserName()), salLtx, "setName");
            });
            //重新计算db中的税款、应发合计、应扣合计、实发合计
            dbDatas.forEach(salLtx -> salLtxService.compute(salLtx));
            //更新
            flag = salLtxService.updateBatchById(dbDatas);
            if (flag) {
                //excel存储到硬盘和change_sheet_attachment表中
                storeExcel(files, comment, Constants.SAL_LTX, dbDatas.get(0).getYear(), dbDatas.get(0).getMonth());
            }
        } else if (Constants.PATH_SAL_LX.equals(path) && Constants.BATCH_MODIFY_YINGFA.equals(type)) { //离休的应发应扣
            //遍历出excel的数据
            List<Object> excelDatas = iterateExcel(files, YingfaYingkouSalLx.class);
            Object obj = (Object) excelDatas;
            List<YingfaYingkouSalLx> yingfaYingkouSalLxs = (List<YingfaYingkouSalLx>) obj;
            HashMap<String, YingfaYingkouSalLx> excelHashMap = Maps.newHashMap();
            //根据姓名从db中取出该人的工资数据
            List<String> excleUserNameList = Lists.newArrayList();
            yingfaYingkouSalLxs.stream().forEach(yingfaYingkouSalLx -> {
                String userName = yingfaYingkouSalLx.getName();
                if (userName != null) {
                    excleUserNameList.add(userName);
                }
                excelHashMap.put(userName, yingfaYingkouSalLx);
            });
            List<SalLx> dbDatas = salLxService.list(new QueryWrapper<SalLx>().eq("finish", Constants.FINISH_STATUS_NO).in("user_name", excleUserNameList));
            //验证excel中的姓名
            List<String> dbUserNameList = Lists.newArrayList();
            dbDatas.forEach(tmp -> dbUserNameList.add(tmp.getUserName()));
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            //将excel中的数据合并到db中
            ReflectUtil reflectUtil = new ReflectUtil();
            dbDatas.forEach(salLx -> {
                CoverObj.cover(reflectUtil, excelHashMap.get(salLx.getUserName()), salLx, "setName");
            });
            //重新计算db中的税款、应发合计、应扣合计、实发合计
            dbDatas.forEach(salLx -> salLxService.compute(salLx));
            //更新
            flag = salLxService.updateBatchById(dbDatas);
            if (flag) {
                //excel存储到硬盘和change_sheet_attachment表中
                storeExcel(files, comment, Constants.SAL_LX, dbDatas.get(0).getYear(), dbDatas.get(0).getMonth());
            }
        } else if (Constants.PATH_SAL_NP.equals(path) && Constants.BATCH_MODIFY_OTHER_BONUS.equals(type)) {//内聘的其他薪金
            //遍历出excel
            List<Object> excelDatas = iterateExcelIncludeHeader(files);
            List<Object> newExcelDatas = Lists.newArrayList();
            //取出表头,其他数据赋值给新集合
            Object headerObj = null;
            for (int i = 0; i < excelDatas.size(); i++) {
                if (i == 0) {
                    headerObj = excelDatas.get(i);
                } else {
                    newExcelDatas.add(excelDatas.get(i));
                }
            }
            ChildrenTableBatchModify header = (ChildrenTableBatchModify) headerObj;
            //excel数据
            Object obj = (Object) newExcelDatas;
            List<ChildrenTableBatchModify> otherBonusList = (List<ChildrenTableBatchModify>) obj;
            //根据姓名从db中取出的SalBonus
            List<String> excleUserNameList = Lists.newArrayList();
            otherBonusList.forEach(otherBonus -> {
                if (otherBonus.getUserName() != null) {
                    excleUserNameList.add(otherBonus.getUserName());
                }
            });
            List<SalBonus> dbSalBonus = salBonusService.getNpSalBonusByUserName(excleUserNameList);
            //薪金数据的唯一标识的映射表
            HashMap<String, SalBonus> dbSalBonusHashMap = Maps.newHashMap();
            dbSalBonus.forEach(salBonus -> {
                //key=name+t_name+sal_id
                dbSalBonusHashMap.put(salBonus.getName() + salBonus.getTName() + salBonus.getSalId(), salBonus);
            });
            //用户名和SalNp的id的映射表
            HashMap<String, Long> userNameToSalNpIdHashMap = Maps.newHashMap();
            List<String> dbUserNameList = Lists.newArrayList();
            List<SalNp> salNps = salNpService.list(new QueryWrapper<SalNp>().in("user_name", excleUserNameList).eq("finish", Constants.FINISH_STATUS_NO));
            for (SalNp salNp : salNps) {
                userNameToSalNpIdHashMap.put(salNp.getUserName(), salNp.getId());
                dbUserNameList.add(salNp.getUserName());
            }
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            // 处理excel数据
            HashMap<String, Object> result = handle(header, otherBonusList, dbSalBonusHashMap, userNameToSalNpIdHashMap, Constants.SAL_NP);
            Set<Long> salBonusIds = (Set<Long>) result.get("salBonusIds");
            List<SalBonus> insertSalBonus = (List<SalBonus>) result.get("insertSalBonus");
            List<SalBonus> noModifySalBonus = (List<SalBonus>) result.get("noModifySalBonus");
            //合并
            List<SalBonus> allSalBonus = Lists.newArrayList();
            allSalBonus.addAll(insertSalBonus);
            allSalBonus.addAll(noModifySalBonus);
            //按照sal_id分类出来
            Map<Long, List<SalBonus>> allSalBonusMap = Maps.newHashMap();
            for (SalBonus salBonus : allSalBonus) {
                List<SalBonus> list = allSalBonusMap.get(salBonus.getSalId());
                if (CollectionUtil.isNotNullOrEmpty(list)) {
                    list.add(salBonus);
                } else {
                    list = Lists.newArrayList();
                    list.add(salBonus);
                    allSalBonusMap.put(salBonus.getSalId(), list);
                }
            }
            //重新计算db中的salNp的税款、应发合计、应扣合计、实发合计
            salNps.forEach(salNp -> salNpService.onlyComputeNoTransactionForOtherBonus(salNp, allSalBonusMap.get(salNp.getId())));
            //更新
            flag = salNpService.updateBatchById(salNps);
            if (flag && CollectionUtil.isNotNullOrEmpty(salBonusIds)) {
                //删除-其他薪金
                flag = salBonusService.removeByIds(salBonusIds);
            }
            if (flag) {
                //插入-其他薪金
                flag = salBonusService.saveBatch(insertSalBonus);
            }
        } else if (Constants.PATH_SAL_LTX.equals(path) && Constants.BATCH_MODIFY_OTHER_BONUS.equals(type)) {//退休的其他薪金
            //遍历出excel
            List<Object> excelDatas = iterateExcelIncludeHeader(files);
            List<Object> newExcelDatas = Lists.newArrayList();
            //取出表头,其他数据赋值给新集合
            Object headerObj = null;
            for (int i = 0; i < excelDatas.size(); i++) {
                if (i == 0) {
                    headerObj = excelDatas.get(i);
                } else {
                    newExcelDatas.add(excelDatas.get(i));
                }
            }
            ChildrenTableBatchModify header = (ChildrenTableBatchModify) headerObj;
            //excel数据
            Object obj = (Object) newExcelDatas;
            List<ChildrenTableBatchModify> otherBonusList = (List<ChildrenTableBatchModify>) obj;
            //根据姓名从db中取出的SalBonus
            List<String> excleUserNameList = Lists.newArrayList();
            otherBonusList.forEach(otherBonus -> {
                if (otherBonus.getUserName() != null) {
                    excleUserNameList.add(otherBonus.getUserName());
                }
            });
            List<SalBonus> dbSalBonus = salBonusService.getLtxSalBonusByUserName(excleUserNameList);
            //薪金数据的唯一标识的映射表
            HashMap<String, SalBonus> dbSalBonusHashMap = Maps.newHashMap();
            dbSalBonus.forEach(salBonus -> {
                //key=name+t_name+sal_id
                dbSalBonusHashMap.put(salBonus.getName() + salBonus.getTName() + salBonus.getSalId(), salBonus);
            });
            //当月的用户名和SalLtx的id的映射表
            HashMap<String, Long> userNameToSalLtxIdHashMap = Maps.newHashMap();
            List<String> dbUserNameList = Lists.newArrayList();
            List<SalLtx> salLtxs = salLtxService.list(new QueryWrapper<SalLtx>().in("user_name", excleUserNameList).eq("finish", Constants.FINISH_STATUS_NO));
            for (SalLtx salLtx : salLtxs) {
                userNameToSalLtxIdHashMap.put(salLtx.getUserName(), salLtx.getId());
                dbUserNameList.add(salLtx.getUserName());
            }
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            // 处理excel数据
            HashMap<String, Object> result = handle(header, otherBonusList, dbSalBonusHashMap, userNameToSalLtxIdHashMap, Constants.SAL_LTX);
            Set<Long> salBonusIds = (Set<Long>) result.get("salBonusIds");
            List<SalBonus> insertSalBonus = (List<SalBonus>) result.get("insertSalBonus");
            List<SalBonus> noModifySalBonus = (List<SalBonus>) result.get("noModifySalBonus");
            if (CollectionUtil.isNotNullOrEmpty(salBonusIds)) {
                //删除-其他薪金
                flag = salBonusService.removeByIds(salBonusIds);
            }
            //插入-其他薪金
            flag = salBonusService.saveBatch(insertSalBonus);
        } else if (Constants.PATH_SAL_LX.equals(path) && Constants.BATCH_MODIFY_OTHER_BONUS.equals(type)) {//离休的其他薪金
            //遍历出excel
            List<Object> excelDatas = iterateExcelIncludeHeader(files);
            List<Object> newExcelDatas = Lists.newArrayList();
            //取出表头,其他数据赋值给新集合
            Object headerObj = null;
            for (int i = 0; i < excelDatas.size(); i++) {
                if (i == 0) {
                    headerObj = excelDatas.get(i);
                } else {
                    newExcelDatas.add(excelDatas.get(i));
                }
            }
            ChildrenTableBatchModify header = (ChildrenTableBatchModify) headerObj;
            //excel数据
            Object obj = (Object) newExcelDatas;
            List<ChildrenTableBatchModify> otherBonusList = (List<ChildrenTableBatchModify>) obj;
            //根据姓名从db中取出的SalBonus
            List<String> excleUserNameList = Lists.newArrayList();
            otherBonusList.forEach(otherBonus -> {
                if (otherBonus.getUserName() != null) {
                    excleUserNameList.add(otherBonus.getUserName());
                }
            });
            List<SalBonus> dbSalBonus = salBonusService.getLtxSalBonusByUserName(excleUserNameList);
            //薪金数据的唯一标识的映射表
            HashMap<String, SalBonus> dbSalBonusHashMap = Maps.newHashMap();
            dbSalBonus.forEach(salBonus -> {
                //key=name+t_name+sal_id
                dbSalBonusHashMap.put(salBonus.getName() + salBonus.getTName() + salBonus.getSalId(), salBonus);
            });
            //当月的用户名和SalLtx的id的映射表
            HashMap<String, Long> userNameToSalLxIdHashMap = Maps.newHashMap();
            List<String> dbUserNameList = Lists.newArrayList();
            List<SalLx> salLxs = salLxService.list(new QueryWrapper<SalLx>().in("user_name", excleUserNameList).eq("finish", Constants.FINISH_STATUS_NO));
            for (SalLx salLx : salLxs) {
                userNameToSalLxIdHashMap.put(salLx.getUserName(), salLx.getId());
                dbUserNameList.add(salLx.getUserName());
            }
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            // 处理excel数据
            HashMap<String, Object> result = handle(header, otherBonusList, dbSalBonusHashMap, userNameToSalLxIdHashMap, Constants.SAL_LX);
            Set<Long> salBonusIds = (Set<Long>) result.get("salBonusIds");
            List<SalBonus> insertSalBonus = (List<SalBonus>) result.get("insertSalBonus");
            List<SalBonus> noModifySalBonus = (List<SalBonus>) result.get("noModifySalBonus");
            if (CollectionUtil.isNotNullOrEmpty(salBonusIds)) {
                //删除-其他薪金
                flag = salBonusService.removeByIds(salBonusIds);
            }
            //插入-其他薪金
            flag = salBonusService.saveBatch(insertSalBonus);
        } else if (Constants.PATH_SAL_NP.equals(path)) {   //计税专用项
            String dbType = "";
            if (Constants.BATCH_MODIFY_TAX_ADD.equals(type)) {
                //内聘的计税专用-加项
                dbType = Constants.ADD;
            } else if (Constants.BATCH_MODIFY_TAX_SUBSTRACT.equals(type)) {
                //内聘的计税专用-减项
                dbType = Constants.SUBTRACT;
            }
            //从内聘的其他薪金处拷贝
            //遍历出excel
            List<Object> excelDatas = iterateExcelIncludeHeader(files);
            List<Object> newExcelDatas = Lists.newArrayList();
            //取出表头,其他数据赋值给新集合
            Object headerObj = null;
            for (int i = 0; i < excelDatas.size(); i++) {
                if (i == 0) {
                    headerObj = excelDatas.get(i);
                } else {
                    newExcelDatas.add(excelDatas.get(i));
                }
            }
            ChildrenTableBatchModify header = (ChildrenTableBatchModify) headerObj;
            //excel数据
            Object obj = (Object) newExcelDatas;
            List<ChildrenTableBatchModify> jishuiTaxList = (List<ChildrenTableBatchModify>) obj;
            //根据姓名从db中取出的SalNpTax
            List<String> excleUserNameList = Lists.newArrayList();
            jishuiTaxList.forEach(tax -> {
                if (tax.getUserName() != null) {
                    excleUserNameList.add(tax.getUserName());
                }
            });
            List<SalNpTax> dbSalNpTax = salNpTaxService.getSalNpTaxByUserName(excleUserNameList);
            //薪金数据的唯一标识的映射表
            HashMap<String, SalNpTax> dbSalNpTaxHashMap = Maps.newHashMap();
            dbSalNpTax.forEach(salNpTax -> {
                //key=name+type+sal_id
                dbSalNpTaxHashMap.put(salNpTax.getName() + salNpTax.getType() + salNpTax.getSalNpId(), salNpTax);
            });
            //用户名和SalNp的id的映射表
            HashMap<String, Long> userNameToSalNpIdHashMap = Maps.newHashMap();
            List<String> dbUserNameList = Lists.newArrayList();
            List<SalNp> salNps = salNpService.list(new QueryWrapper<SalNp>().in("user_name", excleUserNameList).eq("finish", Constants.FINISH_STATUS_NO));
            for (SalNp salNp : salNps) {
                userNameToSalNpIdHashMap.put(salNp.getUserName(), salNp.getId());
                dbUserNameList.add(salNp.getUserName());
            }
            //验证姓名
            List<String> differentUserNameList = CollectionUtil.differentList(excleUserNameList, dbUserNameList);
            if (differentUserNameList.size() > 0) {
                throw new RuntimeException(Joiner.on(",").skipNulls().join(differentUserNameList) + "-不在本月工资表中");
            }
            // 处理excel数据
            HashMap<String, Object> result = handle2(header, jishuiTaxList, dbSalNpTaxHashMap, userNameToSalNpIdHashMap, dbType);
            Set<Long> salNpTaxIds = (Set<Long>) result.get("salNpTaxIds");
            List<SalNpTax> insertSalNpTax = (List<SalNpTax>) result.get("insertSalNpTax");
            List<SalNpTax> noModifySalNpTax = (List<SalNpTax>) result.get("noModifySalNpTax");
            //合并
            List<SalNpTax> allSalNpTax = Lists.newArrayList();
            allSalNpTax.addAll(insertSalNpTax);
            allSalNpTax.addAll(noModifySalNpTax);
            //按照sal_id分类出来
            Map<Long, List<SalNpTax>> allSalNpTaxMap = Maps.newHashMap();
            for (SalNpTax salNpTax : allSalNpTax) {
                List<SalNpTax> list = allSalNpTaxMap.get(salNpTax.getSalNpId());
                if (CollectionUtil.isNotNullOrEmpty(list)) {
                    list.add(salNpTax);
                } else {
                    list = Lists.newArrayList();
                    list.add(salNpTax);
                    allSalNpTaxMap.put(salNpTax.getSalNpId(), list);
                }
            }
            //重新计算db中的salNp的税款、应发合计、应扣合计、实发合计
            salNps.forEach(salNp -> salNpService.onlyComputeNoTransactionForJishui(salNp, allSalNpTaxMap.get(salNp.getId())));
            //更新
            flag = salNpService.updateBatchById(salNps);
            if (flag && CollectionUtil.isNotNullOrEmpty(salNpTaxIds)) {
                //删除-计税专用项
                flag = salNpTaxService.removeByIds(salNpTaxIds);
            }
            if (flag) {
                //插入-计税专用项
                flag = salNpTaxService.saveBatch(insertSalNpTax);
            }
        }
        return flag;
    }


    private List<Object> iterateExcel(MultipartFile[] files, Class objClass) {
        MultipartFile multipartFile = null;
        List<Object> datas = Lists.newArrayList();
        for (int i = 0; i < files.length; i++) {
            multipartFile = files[i];
            ExcelUtil.readExcel(multipartFile, objClass, datas);
        }
        if (CollectionUtil.isNotNullOrEmpty(datas)) {
            //合并excel的数据
            int count = datas.size();
            Set<Object> set = new HashSet<>(datas);
            if (count == set.size()) {
                //姓名没有重复
                return datas;
            } else {
                //以后这里需要继续处理一下，是哪些姓名有重复了
                log.error("姓名有重复");
                throw new RuntimeException("姓名有重复");
            }
        } else {
            log.error("上传的excel为空");
            throw new RuntimeException("上传的excel为空");
        }
    }

    private List<Object> iterateExcelIncludeHeader(MultipartFile[] files) {
        MultipartFile multipartFile = null;
        List<Object> datas = Lists.newArrayList();
        for (int i = 0; i < files.length; i++) {
            multipartFile = files[i];
            ExcelUtil.readExcelIncludeHeader(multipartFile, ChildrenTableBatchModify.class, datas);
        }
        if (CollectionUtil.isNotNullOrEmpty(datas)) {
            //合并excel的数据
            int count = datas.size();
            Set<Object> set = new HashSet<>(datas);
            if (count == set.size()) {
                //姓名没有重复
                return datas;
            } else {
                //以后这里需要继续处理一下，是哪些姓名有重复了
                log.error("姓名有重复");
                throw new RuntimeException("姓名有重复");
            }
        } else {
            log.error("上传的excel为空");
            throw new RuntimeException("上传的excel为空");
        }
    }

    private void storeExcel(MultipartFile[] files, String comment, String tName, Integer year, Integer month) {
        MultipartFile multipartFile = null;
        String fileName = null;
        String directory = Constants.EXCEL_PAHT + YearMonth.getYearMonthInt(year, month) + Constants.FILE_SEPARATOR;
        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }
        List<ChangeSheetAttachment> attachments = Lists.newArrayList();
        for (int i = 0; i < files.length; i++) {
            multipartFile = files[i];
            InputStream inputStream = null;
            try {
                fileName = multipartFile.getOriginalFilename();
                inputStream = multipartFile.getInputStream();
                //excel存储到硬盘
                File excelFile = new File(directory + fileName);
                if (!excelFile.exists()) {
                    multipartFile.transferTo(excelFile);
                } else {
                    String tmp = fileName;
                    int index = tmp.lastIndexOf(".");
                    fileName = tmp.substring(0, index) + YearMonth.getYearMonthInt(year, month) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddHHmm")) + tmp.substring(index);
                    multipartFile.transferTo(new File(directory + fileName));
                }
                //收集ChangeSheetAttachment
                attachments.add(getChangeSheetAttachment(directory, fileName, comment, tName, year, month));
            } catch (Exception e) {
                throw new RuntimeException("保存excel出错了");
            } finally {
                CloseStreamUtil.close(inputStream);
            }
        }
        //excel存储到change_sheet_attachment表中
        if (CollectionUtil.isNotNullOrEmpty(attachments)) {
            changeSheetAttachmentService.saveBatch(attachments);
        } else {
            throw new RuntimeException("保存excel附件出错了");
        }
    }

    private ChangeSheetAttachment getChangeSheetAttachment(String directory, String fileName, String comment, String tName, Integer year, Integer month) {
        ChangeSheetAttachment attachment = new ChangeSheetAttachment();
        attachment.setYear(year)
                .setMonth(month)
                .setFileName(fileName)
                .setPath(directory + fileName)
                .setComment(comment)
                .setTName(tName)
                .setOperateTime(LocalDateTime.now());
        return attachment;
    }

    //取出数据字典中的类目为应发计税、应发不计税的名称，格式：名称-应发计税
    private HashMap<String, String> getTaxNameFromDic() {
        HashMap<String, String> hm = Maps.newHashMap();
        List<SysDic> list = sysDicService.list(new QueryWrapper<SysDic>().in("flag", Constants.YINGFA_TAX, Constants.YINGFA_NO_TAX));
        list.forEach(sysDic -> hm.put(sysDic.getName(), sysDic.getFlag()));
        return hm;
    }

    private HashMap<String, Object> handle(ChildrenTableBatchModify header, List<ChildrenTableBatchModify> OtherBonusList, HashMap<String, SalBonus> dbSalBonusHashMap, HashMap<String, Long> userNameToSalNpIdHashMap, String tName) {
        HashMap<String, Object> hm = Maps.newHashMap();
        //待删除的id
        Set<Long> salBonusIds = Sets.newHashSet();
        //待插入的薪金
        List<SalBonus> insertSalBonus = Lists.newArrayList();
        //应发计税、应发不计税
        HashMap<String, String> tax = getTaxNameFromDic();
        //表头中的薪金名称和索引的映射表
        HashMap<Integer, String> nameIndexHashMap = indexNameHashMap(header);
        //未修改的其他薪金
        List<SalBonus> noModifySalBonus = Lists.newArrayList();
        //处理
        ReflectUtil reflectUtil = new ReflectUtil();
        for (ChildrenTableBatchModify otherBonus : OtherBonusList) {
            String moneyName = null;
            String value = null;
            String comment = null;
            Long sal_id = userNameToSalNpIdHashMap.get(otherBonus.getUserName());
            if (sal_id == null) continue;
            for (int i = 1; i <= 30; i++) {
                value = (String) reflectUtil.invokeReturnValue(otherBonus, "getVar" + i);
                if (!Strings.isNullOrEmpty(value)) {
                    //判断是否是备注
                    if (!value.contains("备注")) {
                        moneyName = nameIndexHashMap.get(i);
                        String type = tax.get(moneyName);
                        if (Constants.SAL_LTX.equals(tName) || Constants.SAL_LX.equals(tName)) {
                            type = "应发不计税";
                        } else {
                            if (null == type) {
                                throw new RuntimeException("没有对应的应发计税、应发不计税!");
                            }
                        }
                        //构造
                        SalBonus excelSalBonus = new SalBonus();
                        excelSalBonus.setName(moneyName);
                        excelSalBonus.setMoney(Double.parseDouble(value));
                        excelSalBonus.setType(type);
                        excelSalBonus.setTName(tName);
                        excelSalBonus.setSalId(sal_id);
                        //备注
                        comment = (String) reflectUtil.invokeReturnValue(otherBonus, "getVar" + (i + 1));
                        if (!Strings.isNullOrEmpty(comment)) {
                            excelSalBonus.setComment(comment);
                        }
                        //key=name+t_name+sal_id
                        String key = moneyName + tName + sal_id;
                        //判断excel数据是否在数据库中存在
                        if (dbSalBonusHashMap.containsKey(key)) {
                            SalBonus dbSalBonus = dbSalBonusHashMap.get(key);
                            //添加待删除的集合中
                            salBonusIds.add(dbSalBonus.getId());
                        }
                        insertSalBonus.add(excelSalBonus);
                    }
                }
            }
            //取出在其他薪金中的没有在excel中修改的数据
            for (Map.Entry<String, SalBonus> entry : dbSalBonusHashMap.entrySet()) {
                SalBonus tmp = entry.getValue();
                if (sal_id.equals(tmp.getSalId()) && !salBonusIds.contains(tmp.getId())) {
                    noModifySalBonus.add(tmp);
                }
            }
        }
        hm.put("salBonusIds", salBonusIds);
        hm.put("insertSalBonus", insertSalBonus);
        hm.put("noModifySalBonus", noModifySalBonus);
        return hm;
    }

    private HashMap<String, Object> handle2(ChildrenTableBatchModify header, List<ChildrenTableBatchModify> jishuiTaxList, HashMap<String, SalNpTax> dbSalNpTaxHashMap, HashMap<String, Long> userNameToSalNpIdHashMap, String type) {
        HashMap<String, Object> hm = Maps.newHashMap();
        //待删除的id
        Set<Long> salNpTaxIds = Sets.newHashSet();
        //待插入的计税专用项
        List<SalNpTax> insertSalNpTax = Lists.newArrayList();
        //表头中的专用项名称和索引的映射表
        HashMap<Integer, String> nameIndexHashMap = indexNameHashMap(header);
        //未修改的计税专用项
        List<SalNpTax> noModifySalNpTax = Lists.newArrayList();
        //处理
        ReflectUtil reflectUtil = new ReflectUtil();
        for (ChildrenTableBatchModify otherBonus : jishuiTaxList) {
            String moneyName = null;
            String value = null;
            Long sal_id = userNameToSalNpIdHashMap.get(otherBonus.getUserName());
            if (sal_id == null) continue;
            for (int i = 1; i <= 30; i++) {
                value = (String) reflectUtil.invokeReturnValue(otherBonus, "getVar" + i);
                if (!Strings.isNullOrEmpty(value)) {
                    moneyName = nameIndexHashMap.get(i);
                    //构造
                    SalNpTax excelSalNpTax = new SalNpTax();
                    excelSalNpTax.setName(moneyName);
                    excelSalNpTax.setMoney(Double.parseDouble(value));
                    excelSalNpTax.setType(type);
                    excelSalNpTax.setSalNpId(sal_id);
                    //key=name+type+sal_id
                    String key = moneyName + type + sal_id;
                    //判断excel数据是否在数据库中存在
                    if (dbSalNpTaxHashMap.containsKey(key)) {
                        SalNpTax dbSalNpTax = dbSalNpTaxHashMap.get(key);
                        //添加待删除的集合中
                        salNpTaxIds.add(dbSalNpTax.getId());
                    }
                    insertSalNpTax.add(excelSalNpTax);
                }
            }
            //取出在计税专用项中的没有在excel中修改的数据
            for (Map.Entry<String, SalNpTax> entry : dbSalNpTaxHashMap.entrySet()) {
                SalNpTax tmp = entry.getValue();
                if (sal_id.equals(tmp.getSalNpId()) && !salNpTaxIds.contains(tmp.getId())) {
                    noModifySalNpTax.add(tmp);
                }
            }
        }
        hm.put("salNpTaxIds", salNpTaxIds);
        hm.put("insertSalNpTax", insertSalNpTax);
        hm.put("noModifySalNpTax", noModifySalNpTax);
        return hm;
    }

    private HashMap<Integer, String> indexNameHashMap(Object header) {
        HashMap<Integer, String> hm = Maps.newHashMap();
        Class clazz = header.getClass();
        Field[] fields = clazz.getDeclaredFields();
        ReflectUtil reflectUtil = new ReflectUtil();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String name = fields[i].getName();
            if (name.startsWith("var")) {
                //首字母转大写
                String firstCharUpperCase = name.substring(0, 1).toUpperCase().concat(name.substring(1).toLowerCase());
                //反射调用获取值
                Object value = reflectUtil.invokeReturnValue(header, "get" + firstCharUpperCase);
                if (value != null) {
                    //取出 var10中的数字
                    String index = name.replaceAll("var", "").trim();
                    hm.put(Integer.parseInt(index), (String) value);
                }
            }
        }
        return hm;
    }
}
