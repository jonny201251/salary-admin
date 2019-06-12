package com.hthyaq.salaryadmin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hthyaq.salaryadmin.entity.SalLtx;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 工资离退休 Mapper 接口
 * </p>
 *
 * @author zhangqiang
 * @since 2018-12-10
 */
public interface SalLtxMapper extends BaseMapper<SalLtx> {
    int updateFinishState(@Param("year") Integer year, @Param("month") Integer month);
}
