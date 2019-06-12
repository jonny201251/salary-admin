package com.hthyaq.salaryadmin.restController;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.entity.SysRole;
import com.hthyaq.salaryadmin.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-17
 */
@Api
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation("根据分页查询出-角色")
    @GetMapping("/list")
    public Page<SysRole> list(Integer pageNum) {
        Page<SysRole> pagination = new Page<>(pageNum, Constants.PAGE_SIZE);
        sysRoleService.page(pagination, null);
        return pagination;
    }

    @ApiOperation("保存和修改-角色")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdateRole(@RequestBody SysRole role) {
        return sysRoleService.saveOrUpdate(role);
    }

}
