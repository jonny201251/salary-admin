package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.TreeSelectOrTable;
import com.hthyaq.salaryadmin.entity.SysDept;
import com.hthyaq.salaryadmin.service.SysDeptService;
import com.hthyaq.salaryadmin.vo.TreeSelectData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Api
@RestController
@RequestMapping("/sysDept")
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @ApiOperation("根据分页查询出部门")
    @GetMapping("/list")
    public Page<SysDept> list(Integer pageNum, String name) {
        Page<SysDept> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        sysDeptService.page(pagination, new QueryWrapper<SysDept>().like("name", name).orderByAsc("pid","sort"));
        return pagination;
    }

    @ApiOperation("根据antd的treeData数据结构查询出部门")
    @GetMapping("/treeSelectData")
    public List<TreeSelectData> treeData() {
        List<SysDept> depts = sysDeptService.list(new QueryWrapper<SysDept>().eq("status","正常").orderByAsc("pid","sort"));
        return TreeSelectOrTable.getTreeSelect(depts);
    }

    @ApiOperation("保存和修改-部门")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateDept(@RequestBody SysDept dept) {
        //create_time
        dept.setCreateTime(LocalDateTime.now());
        return sysDeptService.saveOrUpdateUserAndChangeSheet(dept);
    }
}
