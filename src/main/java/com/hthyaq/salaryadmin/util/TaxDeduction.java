package com.hthyaq.salaryadmin.util;

import java.math.BigDecimal;

//税款的速算扣除数
public class TaxDeduction {
    /***
     * @param money 应纳税所得额
     * @return 税款
     */
    public static Double get(Double money) {
        //税率 速算扣除数
        double shuilv = 0.0, baseNum = 0.0;
        if (money <= 36000) {
            shuilv = 0.03;
            baseNum = 0.0;
        } else if (money <= 144000) {
            shuilv = 0.1;
            baseNum = 2520.0;
        } else if (money <= 300000) {
            shuilv = 0.2;
            baseNum = 16920.0;
        } else if (money <= 420000) {
            shuilv = 0.25;
            baseNum = 31920.0;
        } else if (money <= 660000) {
            shuilv = 0.3;
            baseNum = 52920.0;
        } else if (money <= 960000) {
            shuilv = 0.35;
            baseNum = 85920.0;
        } else {
            shuilv = 0.45;
            baseNum = 181920.0;
        }
        Double tmp = money * shuilv - baseNum;
        //保留小数点的2位，使用四舍五入，如3.145，取3.15
        Double end = new BigDecimal(tmp).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return end <= 0 ? 0.0 : end;
    }

}
