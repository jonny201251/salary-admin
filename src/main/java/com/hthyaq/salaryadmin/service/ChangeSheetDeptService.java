package com.hthyaq.salaryadmin.service;

import com.hthyaq.salaryadmin.entity.ChangeSheetDept;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 变动单-部门，来源于内聘 服务类
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-05
 */
public interface ChangeSheetDeptService extends IService<ChangeSheetDept> {

    boolean saveOrUpdateChangeSheetDept(ChangeSheetDept changeSheetDept);
}
