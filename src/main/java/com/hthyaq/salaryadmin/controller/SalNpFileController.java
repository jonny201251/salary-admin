package com.hthyaq.salaryadmin.controller;

import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.bo.ICBC;
import com.hthyaq.salaryadmin.bo.UpTaxBaseRowModel;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalNpService;
import com.hthyaq.salaryadmin.service.SalNpTaxService;
import com.hthyaq.salaryadmin.util.CollectionUtil;
import com.hthyaq.salaryadmin.util.Constants;
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

import static java.util.Optional.ofNullable;

//导出excel
@Api
@Controller
@RequestMapping("/salNpFile")
public class SalNpFileController {
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalNpTaxService salNpTaxService;
    @Autowired
    SalBonusService salBonusService;
    private String type = "内聘";

    /*
	站发-应处理金额=实发
	院发-应处理金额=应发合计-应扣合计-房租-扣款-税款2
	院发人员的站发部分-应处理金额=其他薪金（应发计税）-税款1
     */
    @ApiOperation("导出工行数据")
    @GetMapping("/writeExcel")
    public void writeExcel(HttpServletResponse response) throws Exception {
        List<SalNp> salNpList = salNpService.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO).orderByAsc("user_sort"));
        List<Long> salIds = Lists.newArrayList();
        salNpList.forEach(salNp -> salIds.add(salNp.getId()));
        //其他薪金（应发计税）
        List<SalBonus> salBonuslist = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_NP).eq("type", Constants.YINGFA_TAX).in("sal_id", salIds));
        Map<Long, Double> salBonusMap = Maps.newHashMap();
        for (SalBonus salBonus : salBonuslist) {
            Double money = salBonusMap.get(salBonus.getSalId());
            if (money == null) {
                salBonusMap.put(salBonus.getSalId(), salBonus.getMoney());
            } else {
                money += salBonus.getMoney();
                salBonusMap.put(salBonus.getSalId(), money);
            }
        }
        if (CollectionUtil.isNotNullOrEmpty(salNpList)) {
            List<ICBC> zhanList = Lists.newArrayList();
            List<ICBC> yuanList = Lists.newArrayList();
            List<ICBC> yuan2List = Lists.newArrayList();
            salNpList.forEach(salNp -> {
                if (Constants.GIVE_MODE_ZHAN.equals(salNp.getUserGiveMode())) {
                    ICBC bankData = new ICBC();
                    bankData.setUserName(salNp.getUserName())
                            .setUserBankAccount(salNp.getUserBankAccount());
                    bankData.setShouldMoney(salNp.getShifa());
                    zhanList.add(bankData);
                } else {
                    ICBC bankData1 = new ICBC();
                    bankData1.setUserName(salNp.getUserName())
                            .setUserBankAccount(salNp.getUserBankAccount());
                    bankData1.setShouldMoney(salNp.getYingfa() - salNp.getYingkou() - salNp.getFangzu() - salNp.getKoukuan() - salNp.getShuikuan2());
                    yuanList.add(bankData1);
                    ICBC bankData2 = new ICBC();
                    bankData2.setUserName(salNp.getUserName())
                            .setUserBankAccount(salNp.getUserBankAccount());
                    Double shouldMoney = ofNullable(salBonusMap.get(salNp.getId())).orElse(0.0) - salNp.getShuikuan1();
                    bankData2.setShouldMoney(shouldMoney < 0 ? 0 : shouldMoney);
                    yuan2List.add(bankData2);
                }
            });
            Map<String, List<? extends BaseRowModel>> dataMap = Maps.newHashMap();
            if (CollectionUtil.isNotNullOrEmpty(zhanList)) {
                dataMap.put("站发", zhanList);
            }
            if (CollectionUtil.isNotNullOrEmpty(yuanList)) {
                dataMap.put("院发", yuanList);
            }
            if (CollectionUtil.isNotNullOrEmpty(yuan2List)) {
                dataMap.put("院发人员的站发部分", yuan2List);
            }

            String file = Constants.TMP_PAHT + Constants.FILE_SEPARATOR + salNpList.get(0).getYear() + "年" + salNpList.get(0).getMonth() + "月-" + type + "-工行数据.xlsx";
            //先删除
            File fileTmp = new File(file);
            if (fileTmp.exists()) {
                fileTmp.delete();
            }
            ExcelUtil.generateExcelMoreSheet(file, dataMap, ICBC.class);
            new ResponseExcel().download(file, response);
        }
    }

    /*
    站发-本期收入=应发合计+其他薪金合计+计税加项
	院发-本期收入=应发合计+计税加项
	院发人员的站发部分-本期收入=其他薪金合计
     */
    @ApiOperation("导出报税数据")
    @GetMapping("/writeUpTaxExcel")
    public void writeUpTaxExcel(HttpServletResponse response) throws Exception {
        //内聘工资
        List<SalNp> salNpList = salNpService.list(new QueryWrapper<SalNp>().eq("finish", Constants.FINISH_STATUS_NO));
        List<Long> salIds = Lists.newArrayList();
        salNpList.forEach(salNp -> salIds.add(salNp.getId()));
        //其他薪金（应发不计税）
        List<SalBonus> salBonuslist = salBonusService.list(new QueryWrapper<SalBonus>().eq("t_name", Constants.SAL_NP).eq("type", Constants.YINGFA_NO_TAX).in("sal_id", salIds));
        Map<Long, Double> salBonusMap = Maps.newHashMap();
        for (SalBonus salBonus : salBonuslist) {
            Double money = salBonusMap.get(salBonus.getSalId());
            if (money == null) {
                salBonusMap.put(salBonus.getSalId(), salBonus.getMoney());
            } else {
                money += salBonus.getMoney();
                salBonusMap.put(salBonus.getSalId(), money);
            }
        }
        //计税专用项
        List<SalNpTax> salNpTaxList = salNpTaxService.list(new QueryWrapper<SalNpTax>().in("sal_np_id", salIds));
        //计税加项的key=sal_np_id+加项，累计X=sal_np_id+减项+累计X
        String key = null;
        Double money = null;
        Map<String, Double> salNpTaxMap = Maps.newHashMap();
        for (SalNpTax salNpTax : salNpTaxList) {
            String type = salNpTax.getType();
            if (Constants.ADD.equals(type)) {
                key = salNpTax.getSalNpId() + Constants.ADD;
            } else {
                key = salNpTax.getSalNpId() + Constants.SUBTRACT + salNpTax.getName();
            }
            money = salNpTaxMap.get(key);
            if (money == null) {
                salNpTaxMap.put(key, salNpTax.getMoney());
            } else {
                money += salNpTax.getMoney();
                salNpTaxMap.put(key, money);
            }
        }
        if (CollectionUtil.isNotNullOrEmpty(salNpList)) {
            List<UpTaxBaseRowModel> zhanList = Lists.newArrayList();
            List<UpTaxBaseRowModel> yuanList = Lists.newArrayList();
            List<UpTaxBaseRowModel> yuan2List = Lists.newArrayList();
            for (SalNp salNp : salNpList) {
                if (Constants.GIVE_MODE_ZHAN.equals(salNp.getUserGiveMode())) {
                    UpTaxBaseRowModel upTaxBaseRowModel = new UpTaxBaseRowModel();
                    upTaxBaseRowModel.setName(salNp.getUserName())
                            .setType("居民身份证")
                            .setIdNum(salNp.getUserIdNum())
                            .setIncome(salNp.getYingfa() + salNp.getJiangjin() + ofNullable(salNpTaxMap.get(salNp.getId() + Constants.ADD)).orElse(0.0))
                            .setNoTax(ofNullable(salBonusMap.get(salNp.getId())).orElse(0.0))
                            .setYanglao(salNp.getYanglao())
                            .setYiliao(salNp.getYiliao())
                            .setShiye(salNp.getShiye())
                            .setZhufang(salNp.getZhufang())
                            .setVar1(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计子女教育支出扣除"))
                            .setVar2(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计继续教育支出扣除"))
                            .setVar3(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计住房贷款利息支出扣除"))
                            .setVar4(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计住房租金支出扣除"))
                            .setVar5(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计赡养老人支出扣除"))
                            .setVar6(salNp.getNianjin())
                            .setVar7(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "商业健康保险"));
                    zhanList.add(upTaxBaseRowModel);
                } else {
                    UpTaxBaseRowModel upTaxBaseRowModel1 = new UpTaxBaseRowModel();
                    upTaxBaseRowModel1.setName(salNp.getUserName())
                            .setType("居民身份证")
                            .setIdNum(salNp.getUserIdNum())
                            .setIncome(salNp.getYingfa()+ ofNullable(salNpTaxMap.get(salNp.getId() + Constants.ADD)).orElse(0.0))
//                            .setNoTax(ofNullable(salBonusMap.get(salNp.getId())).orElse(0.0))
                            .setNoTax(0.0)
                            .setYanglao(salNp.getYanglao())
                            .setYiliao(salNp.getYiliao())
                            .setShiye(salNp.getShiye())
                            .setZhufang(salNp.getZhufang())
                            .setVar1(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计子女教育支出扣除"))
                            .setVar2(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计继续教育支出扣除"))
                            .setVar3(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计住房贷款利息支出扣除"))
                            .setVar4(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计住房租金支出扣除"))
                            .setVar5(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "累计赡养老人支出扣除"))
                            .setVar6(salNp.getNianjin())
                            .setVar7(salNpTaxMap.get(salNp.getId() + Constants.SUBTRACT + "商业健康保险"));
                    yuanList.add(upTaxBaseRowModel1);
                    UpTaxBaseRowModel upTaxBaseRowModel2 = new UpTaxBaseRowModel();
                    upTaxBaseRowModel2.setName(salNp.getUserName())
                            .setType("居民身份证")
                            .setIdNum(salNp.getUserIdNum())
                            .setIncome(salNp.getJiangjin())
                            .setNoTax(ofNullable(salBonusMap.get(salNp.getId())).orElse(0.0));
                    yuan2List.add(upTaxBaseRowModel2);
                }
            }

            Map<String, List<? extends BaseRowModel>> dataMap = Maps.newHashMap();
            if (CollectionUtil.isNotNullOrEmpty(zhanList)) {
                dataMap.put("站发", zhanList);
            }
            if (CollectionUtil.isNotNullOrEmpty(yuanList)) {
                dataMap.put("院发", yuanList);
            }
            if (CollectionUtil.isNotNullOrEmpty(yuan2List)) {
                dataMap.put("院发人员的站发部分", yuan2List);
            }

            String file = Constants.TMP_PAHT + Constants.FILE_SEPARATOR + salNpList.get(0).getYear() + "年" + salNpList.get(0).getMonth() + "月-" + type + "-报税数据.xlsx";
            //先删除
            File fileTmp = new File(file);
            if (fileTmp.exists()) {
                fileTmp.delete();
            }
            ExcelUtil.generateExcelMoreSheet(file, dataMap, UpTaxBaseRowModel.class);
            new ResponseExcel().download(file, response);
        }
    }

}
