package com.hthyaq.salaryadmin.restController;


import com.hthyaq.salaryadmin.entity.ChangeSheetDept;
import com.hthyaq.salaryadmin.service.ChangeSheetDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 变动单-部门，来源于内聘 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
@Api
@RestController
@RequestMapping("/changeSheetDept")
public class ChangeSheetDeptController {
    @Autowired
    ChangeSheetDeptService changeSheetDeptService;

    @ApiOperation("保存和修改-部门重组")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateChangeSheetDept(@RequestBody ChangeSheetDept changeSheetDept){
        return changeSheetDeptService.saveOrUpdateChangeSheetDept(changeSheetDept);
    }
}
