package com.hthyaq.salaryadmin.util;

import com.google.common.collect.Maps;

import java.util.HashMap;

public class SalColumnToChinese {
    private static HashMap<String,String> salNp= Maps.newHashMap();
    private static HashMap<String,String> salLtx= Maps.newHashMap();
    private static HashMap<String,String> salLx= Maps.newHashMap();
    static{
        //内聘
        salNp.put("gangwei","岗位工资");
        salNp.put("xinji","薪级工资");
        salNp.put("bucha","补差");
        salNp.put("bunei","部内");
        salNp.put("fudong","浮动");
        salNp.put("hangling","航龄");
        salNp.put("gongbu","工补");
        salNp.put("shuxi","书洗");
        salNp.put("caimo","菜磨");
        salNp.put("zhibu","职补1-9");
        salNp.put("baoliu","10%保留");
        salNp.put("jidu","季度效益");
        salNp.put("shuidian","水电燃补");
        salNp.put("xiaoyi","效益补贴");
        salNp.put("gangjin","岗津");
        salNp.put("danshengbu","单身补");
        salNp.put("qita","其他");
        salNp.put("fangzu","房租");
        salNp.put("yanglao","养老保险");
        salNp.put("zhufang","住房");
        salNp.put("shiye","失业保险");
        salNp.put("koukuan","扣款");
        salNp.put("yiliao","医疗保险");
        //离退休
        salLtx.put("jiben","基本退休费");
        salLtx.put("guifan","规范补贴");
        salLtx.put("baoliu","保留补贴");
        salLtx.put("butie","171补贴");
        salLtx.put("shubao","书报洗理费");
        salLtx.put("tizu","提租");
        salLtx.put("tiao","34-39调");
        salLtx.put("bucha","站内补差");
        salLtx.put("zengzi","1617增资");
        salLtx.put("yingfaqita","应发其他");
        salLtx.put("fangzu","房租");
        salLtx.put("yingkouqita","应扣其他");
        //离休
        salLx.put("jiben","基本离休费");
        salLx.put("butie1","离休人员补贴");
        salLx.put("huli","护理费");
        salLx.put("dianhua","电话费");
        salLx.put("jiaotong","交通费");
        salLx.put("butie2","171补贴");
        salLx.put("shubao","书报洗理");
        salLx.put("butie3","提租补贴");
        salLx.put("tiaozi1","14年调资");
        salLx.put("tiaozi2","34-39年调资");
        salLx.put("bucha","站内补差");
        salLx.put("zengzi","1612增资");
        salLx.put("yingfaqita","应发其他");
        salLx.put("fangzu","房租");
        salLx.put("yingkouqita","应扣其他");

    }
    public static String get(String column,String t_name){
        if(Constants.SAL_NP.equals(t_name)){
            return salNp.get(column);
        }else if(Constants.SAL_LTX.equals(t_name)){
            return salLtx.get(column);
        }else{
            return salLx.get(column);
        }
    }
}
