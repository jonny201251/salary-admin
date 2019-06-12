package com.hthyaq.salaryadmin.vo;

import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalLtx;
import lombok.Data;
import lombok.experimental.Accessors;

//离退休工资的页面数据
@Data
@Accessors(chain = true)
public class SalLtxPageData extends SalLtx {
    private Repeater<SalBonus> yingfajiangjin_repeater;
}
