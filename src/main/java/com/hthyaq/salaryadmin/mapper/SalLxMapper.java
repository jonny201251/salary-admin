package com.hthyaq.salaryadmin.mapper;

import com.hthyaq.salaryadmin.entity.SalLx;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 工资离休 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2019-04-24
 */
public interface SalLxMapper extends BaseMapper<SalLx> {
    int updateFinishState(@Param("year") Integer year, @Param("month") Integer month);
}
