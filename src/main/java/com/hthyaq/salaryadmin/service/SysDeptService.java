package com.hthyaq.salaryadmin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hthyaq.salaryadmin.entity.SysDept;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
public interface SysDeptService extends IService<SysDept> {
    public boolean saveOrUpdateUserAndChangeSheet(SysDept sysDept);
}
