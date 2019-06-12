package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.SalNp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 工资-内聘表 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-03
 */
public interface SalNpMapper extends BaseMapper<SalNp> {
    int updateFinishState(@Param("year") Integer year, @Param("month") Integer month);
}
