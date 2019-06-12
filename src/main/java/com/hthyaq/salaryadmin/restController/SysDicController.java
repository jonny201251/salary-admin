package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.entity.SysDic;
import com.hthyaq.salaryadmin.service.SysDicService;
import com.hthyaq.salaryadmin.vo.SelectData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 数据字典，包含职工类别、计税类别 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
@Api
@RestController
@RequestMapping("/sysDic")
public class SysDicController {
    @Autowired
    private SysDicService sysDicService;

    @ApiOperation("根据分页查询出-数据字典")
    @GetMapping("/list")
    public Page<SysDic> list(Integer pageNum,@RequestParam(defaultValue = "") String flag, @RequestParam(defaultValue = "") String name) {
        Page<SysDic> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        sysDicService.page(pagination, new QueryWrapper<SysDic>().orderByAsc("sort").like("flag", flag).like("name", name));
        return pagination;
    }

    @ApiOperation("保存和修改-数据字典")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateDic(@RequestBody SysDic dic) {
        if(dic.getId()==null){
            //flag和name不能重复
            QueryWrapper<SysDic> queryWrapper=new QueryWrapper<SysDic>().eq("flag",dic.getFlag()).eq("name",dic.getName());
            List<SysDic> list = sysDicService.list(queryWrapper);
            if(list.size()>0){
                throw new RuntimeException("类目和名称重复了!");
            }
        }
        //create_time
        dic.setCreateTime(LocalDateTime.now());
        return sysDicService.saveOrUpdate(dic);
    }

    @ApiOperation("根据标志位-数据字典")
    @GetMapping("/flagData")
    public List<SelectData> flagList(String flag) {
        List<SysDic> list = sysDicService.list(new QueryWrapper<SysDic>().eq("flag", flag).orderByAsc("sort"));
        List<SelectData> selectDatas= Lists.newArrayList();
        list.stream().forEach(sysDic -> {
            SelectData selectData=new SelectData();
            selectData.setLabel(sysDic.getName()).setValue(sysDic.getName());
            selectDatas.add(selectData);
        });
        return selectDatas;
    }
}
