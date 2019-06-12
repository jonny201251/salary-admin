package com.hthyaq.salaryadmin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import com.hthyaq.salaryadmin.service.SalBonusService;
import com.hthyaq.salaryadmin.service.SalNpTaxService;
import com.hthyaq.salaryadmin.service.UploadFileBatchModifyService;
import com.hthyaq.salaryadmin.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

//用于内聘、离退休的批量修改
@Api
@Controller
@RequestMapping("/uploadFiles")
public class UploadFileBatchModifyController {
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SalNpTaxService salNpTaxService;
    @Autowired
    UploadFileBatchModifyService uploadFileBatchModifyService;

    @ApiOperation("上传多个excel")
    @PostMapping("/upload")
    @ResponseBody
    public boolean upload(@RequestParam("files") MultipartFile[] files, String type, String path, @RequestParam(defaultValue = "") String comment) {
        boolean flag = uploadFileBatchModifyService.completeUpload(files, type, path, comment);
        //删除 其他薪金、计税专用--金额0的时候的情况
        salBonusService.remove(new QueryWrapper<SalBonus>().eq("money", 0.0).notIn("name", Constants.TAX_COLUMNS));
        salNpTaxService.remove(new QueryWrapper<SalNpTax>().eq("money", 0.0).notIn("name", Constants.TAX_COLUMNS));
        return flag;
    }
}
