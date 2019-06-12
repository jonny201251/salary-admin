package com.hthyaq.salaryadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.bo.ICBC;
import com.hthyaq.salaryadmin.entity.SalLx;
import com.hthyaq.salaryadmin.service.SalLxService;
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

//导出excel
@Api
@Controller
@RequestMapping("/salLxFile")
public class SalLxFileController {
    @Autowired
    private SalLxService salLxService;
    String type = "离休";

    @ApiOperation("导出工行数据")
    @GetMapping("/writeExcel")
    public void writeExcel(HttpServletResponse response) throws Exception {
        List<SalLx> list = salLxService.list(new QueryWrapper<SalLx>().eq("finish", Constants.FINISH_STATUS_NO).orderByAsc("user_sort"));
        if (CollectionUtil.isNotNullOrEmpty(list)) {
            List<ICBC> dataList = Lists.newArrayList();
            list.forEach(salLx -> {
                ICBC bankData = new ICBC();
                bankData.setUserName(salLx.getUserName().replaceAll("\\d*",""))
                        .setUserBankAccount(salLx.getUserBankAccount())
                        .setShouldMoney(salLx.getShifa());
                dataList.add(bankData);
            });
            String file = Constants.TMP_PAHT + Constants.FILE_SEPARATOR + list.get(0).getYear() + "年" + list.get(0).getMonth() + "月-" + type + "-工行数据.xlsx";
            //先删除
            File fileTmp = new File(file);
            if (fileTmp.exists()) {
                fileTmp.delete();
            }
            ExcelUtil.generateExcel(file, dataList, ICBC.class);
            new ResponseExcel().download(file, response);
        }
    }
}
