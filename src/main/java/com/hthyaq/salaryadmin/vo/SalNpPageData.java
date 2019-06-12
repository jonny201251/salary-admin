package com.hthyaq.salaryadmin.vo;

import com.hthyaq.salaryadmin.entity.SalBonus;
import com.hthyaq.salaryadmin.entity.SalNp;
import com.hthyaq.salaryadmin.entity.SalNpTax;
import lombok.Data;
import lombok.experimental.Accessors;

//内聘工资的页面数据
@Data
@Accessors(chain = true)
public class SalNpPageData extends SalNp {
    private Repeater<SalBonus> yingfajiangjin_repeater;
    private Repeater<SalNpTax> jishui_add_repeater;
    private Repeater<SalNpTax> jishui_subtract_repeater;
}
