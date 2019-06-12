package com.hthyaq.salaryadmin.controller.initData;

import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hthyaq.salaryadmin.entity.*;
import com.hthyaq.salaryadmin.service.*;
import com.hthyaq.salaryadmin.util.Constants;
import com.hthyaq.salaryadmin.util.Md5Util;
import com.hthyaq.salaryadmin.util.excel.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

//初始化工资数据（用户、部门、内聘工资、离退休工资）
@Api
@RequestMapping("/initData")
@Slf4j
public class InitDataController {
    @Autowired
    SysDeptService SysDeptService;
    @Autowired
    SysUserService SysUserService;
    @Autowired
    SalNpService salNpService;
    @Autowired
    SalBonusService salBonusService;
    @Autowired
    SalLtxService salLtxService;

    public static void main(String[] args) throws Exception {
        InitDataController i = new InitDataController();
        List<Object> sal = i.getExcelData("D:\\work\\工资系统\\初始化工资数据\\院发人员.xlsx", "院发人员.xlsx", UserNameInfo.class);
        Object obj = (Object) sal;
        List<UserNameInfo> salList = (List<UserNameInfo>) obj;
        StringBuilder sb = new StringBuilder();
        for (UserNameInfo tmp : salList) {
            if (!Strings.isNullOrEmpty(tmp.getName())) {
                sb.append("'" + tmp.getName() + "',");
            }
        }
        System.out.println(sb.toString());
    }

    @ApiOperation("1月份的其他薪金.xlsx")
    @GetMapping("/other1")
    public void other1() throws Exception {
        List<Object> sal = getExcelData("D:\\work\\工资系统\\初始化工资数据\\1到4月份的退休工资\\1月份的其他薪金.xlsx", "1月份的其他薪金.xlsx", NpSalMoney.class);
        Object obj = (Object) sal;
        List<NpSalMoney> salList = (List<NpSalMoney>) obj;
        List<SalBonus> list = Lists.newArrayList();
        for (NpSalMoney tmp : salList) {
            String name = tmp.getName();
            SalLtx one = salLtxService.getOne(new QueryWrapper<SalLtx>().eq("user_name", name).eq("month", 1));
            SalBonus SalBonus = new SalBonus();
            SalBonus.setName("茶话费及纪念品费用");
            SalBonus.setMoney(tmp.getMoney());
            SalBonus.setType("应发不计税");
            SalBonus.setTName("sal_ltx");
            SalBonus.setSalId(one.getId());
            list.add(SalBonus);
        }
        salBonusService.saveBatch(list);
    }

    @ApiOperation("退休的工资数据")
    @GetMapping("/tx")
    public void tx() throws Exception {
        List<Object> sal = getExcelData("D:\\work\\工资系统\\初始化工资数据\\1到4月份的退休工资\\4月份的工资.xlsx", "4月份的工资.xlsx", TxSal.class);
        Object obj = (Object) sal;
        List<TxSal> salList = (List<TxSal>) obj;
        List<SalLtx> list = Lists.newArrayList();
        for (TxSal txSal : salList) {
            String name = txSal.getName();
            if (!Strings.isNullOrEmpty(name)) {
                SalLtx dbSalLtx = new SalLtx();
                BeanUtils.copyProperties(dbSalLtx, txSal);
                Double yingfaSum = yingfaSum(dbSalLtx);
                Double yingkouSum = yingkouSum(dbSalLtx);
                Double shifa = yingfaSum - yingkouSum;
                dbSalLtx.setYingfa(yingfaSum);
                dbSalLtx.setYingkou(yingkouSum);
                dbSalLtx.setShifa(shifa);
                SysUser sysUser = SysUserService.getOne(new QueryWrapper<SysUser>().eq("name", name));
    /*            if(sysUser==null){
                    System.out.println(name);
                    continue;
                }*/
                dbSalLtx.setUserId(sysUser.getId());
                dbSalLtx.setUserName(sysUser.getName());
                dbSalLtx.setUserNum(sysUser.getNum());
                dbSalLtx.setUserCategory(sysUser.getCategory());
                dbSalLtx.setUserBankAccount(sysUser.getBankAccount());
                dbSalLtx.setUserStatus(sysUser.getStatus());
                dbSalLtx.setUserJob(sysUser.getJob());
                dbSalLtx.setUserSort(sysUser.getSort());
                dbSalLtx.setUserDeptId(sysUser.getDeptId());
                dbSalLtx.setUserDeptName(sysUser.getDeptName());
                dbSalLtx.setUserOrg(sysUser.getOrg());
                //时间
                dbSalLtx.setYear(2019);
                dbSalLtx.setMonth(4);
                dbSalLtx.setYearmonthInt(201904);
                dbSalLtx.setYearmonthString("2019年4月");
                //
                dbSalLtx.setFinish("已月结");

                list.add(dbSalLtx);
            }
        }
        salLtxService.saveBatch(list);
    }


    @ApiOperation("验证退休的用户")
    @GetMapping("/tuixiu_user")
    public void tuixiu_user() throws Exception {
        List<String> names = Lists.newArrayList();
        names.add("汪玉琨");
        names.add("高俊民");
        names.add("符余三");

        int num = 231;
        //准备存入db
        List<SysUser> sysUserList = Lists.newArrayList();
        for (String name : names) {
            SysUser sysUser = new SysUser();
            sysUser.setName(name);
            sysUser.setNum("无" + num++);
            sysUser.setPwd(Md5Util.encryPassword("123"));
            sysUser.setJob("不在职之离休");
            sysUserList.add(sysUser);
            sysUser.setOrg("航天海鹰安全");
            //部门
            sysUser.setDeptId(80);
            sysUser.setDeptName("其他");
        }
        SysUserService.saveBatch(sysUserList);
    }

    @ApiOperation("内聘-其他薪金")
    @GetMapping("/np_other")
    public boolean np_other() throws Exception {
        List<Object> sal = getExcelData("D:\\work\\工资系统\\初始化工资数据\\李燕妮\\1和2月份的其他薪金\\1月其他薪金.xlsx", "1月其他薪金.xlsx", NpSalMoney.class);
        Object obj = (Object) sal;
        List<NpSalMoney> salList = (List<NpSalMoney>) obj;
        //取出内聘的工资数据
        List<SalNp> salNps = salNpService.list(new QueryWrapper<>());
        HashMap<String, Long> salIds = Maps.newHashMap();
        for (SalNp salNp : salNps) {
            salIds.put(salNp.getUserName(), salNp.getId());
        }
        //处理其他薪金的excel
        List<SalBonus> bonuses = Lists.newArrayList();
        for (NpSalMoney npSalMoney : salList) {
            if (npSalMoney.getName() != null) {
                Long salId = salIds.get(npSalMoney.getName());
                if (salId == null) {
                    System.out.println(npSalMoney.getName() + "-------------------------");
                    throw new RuntimeException("aaa");
                }
                SalBonus salBonus = new SalBonus();
                salBonus.setName("奖金");
                salBonus.setMoney(npSalMoney.getMoney());
                salBonus.setType("应发计税");
                salBonus.setTName(Constants.SAL_NP);
                salBonus.setSalId(salId);

                bonuses.add(salBonus);
            }

        }
        salBonusService.saveBatch(bonuses);
        return true;
    }


    @ApiOperation("9-12月的内聘工资数据")
    @GetMapping("/np_sal9_12")
    public boolean np_sal9_12() throws Exception {
        salNpService.remove(new QueryWrapper<SalNp>().eq("year", 2018).eq("month", 12));

        boolean flag = true;
        List<Object> sal = getExcelData("D:\\work\\工资系统\\初始化工资数据\\李燕妮\\9-12月工资\\12月工资台账.xls", "12月工资台账.xls", NpSal9and12.class);
        Object obj = (Object) sal;
        List<NpSal9and12> salList = (List<NpSal9and12>) obj;
        //将NpSal拷贝到salNp
        List<SalNp> salNps = Lists.newArrayList();
        for (NpSal9and12 npSal : salList) {
            SalNp salNp = new SalNp();
            BeanUtils.copyProperties(salNp, npSal);
            //填充用户信息
            SysUser sysUser = SysUserService.getOne(new QueryWrapper<SysUser>().eq("name", npSal.getName()));
            if (null == sysUser) {
                System.out.println(npSal.getName());
                salNp.setUserName(npSal.getName());
                salNp.setUserOrg("航天海鹰安全");
            } else {
                salNp.setUserId(sysUser.getId());
                salNp.setUserName(sysUser.getName());
                salNp.setUserNum(sysUser.getNum());
                salNp.setUserCategory(sysUser.getCategory());
                salNp.setUserIdNum(sysUser.getIdNum());
                salNp.setUserBankAccount(sysUser.getBankAccount());
                salNp.setUserStatus(sysUser.getStatus());
                salNp.setUserJob(sysUser.getJob());
                salNp.setUserSort(sysUser.getSort());
                salNp.setUserDeptId(sysUser.getDeptId());
                salNp.setUserDeptName(sysUser.getDeptName());
                salNp.setUserOrg(sysUser.getOrg());
            }
            //时间
            salNp.setYear(2018);
            salNp.setMonth(12);
            salNp.setYearmonthInt(201812);
            salNp.setYearmonthString("2018年12月");
            //
            salNp.setFinish("已月结");

            salNps.add(salNp);
        }
        System.out.println(salNps.size());
        flag = salNpService.saveBatch(salNps);
        return flag;
    }

    @ApiOperation("内聘工资数据")
    @GetMapping("/np_sal")
    public boolean np_sal() throws Exception {
        boolean flag = true;
        List<Object> sal = getExcelData("D:\\work\\工资系统\\初始化工资数据\\李燕妮\\9-1月工资\\1月工资台账哦.xls", "1月工资台账哦.xls", NpSal.class);
        Object obj = (Object) sal;
        List<NpSal> salList = (List<NpSal>) obj;
        //将NpSal拷贝到salNp
        List<SalNp> salNps = Lists.newArrayList();
        for (NpSal npSal : salList) {
            SalNp salNp = new SalNp();
            BeanUtils.copyProperties(salNp, npSal);
            //填充用户信息
            SysUser sysUser = SysUserService.getOne(new QueryWrapper<SysUser>().eq("name", npSal.getName()));
            salNp.setUserId(sysUser.getId());
            salNp.setUserName(sysUser.getName());
            salNp.setUserNum(sysUser.getNum());
            salNp.setUserCategory(sysUser.getCategory());
            salNp.setUserIdNum(sysUser.getIdNum());
            salNp.setUserBankAccount(sysUser.getBankAccount());
            salNp.setUserStatus(sysUser.getStatus());
            salNp.setUserJob(sysUser.getJob());
            salNp.setUserSort(sysUser.getSort());
            salNp.setUserDeptId(sysUser.getDeptId());
            salNp.setUserDeptName(sysUser.getDeptName());
            salNp.setUserOrg(sysUser.getOrg());
            //时间
            salNp.setYear(2019);
            salNp.setMonth(1);
            salNp.setYearmonthInt(201901);
            salNp.setYearmonthString("2019年1月");
            //
            salNp.setFinish("未月结");

            salNps.add(salNp);
        }
        flag = salNpService.saveBatch(salNps);
        return flag;
    }


    @ApiOperation("内聘用户数据")
    @GetMapping("/np_user")
    public boolean np_user() throws Exception {
        boolean flag = true;
        //编制
        List<Object> bianzhi = getExcelData("D:\\work\\工资系统\\初始化工资数据\\沈亚超\\内聘的编制.xlsx", "内聘的编制.xlsx");
        Object obj1 = (Object) bianzhi;
        List<UserNameInfo> bianzhiList = (List<UserNameInfo>) obj1;
        HashMap<String, String> bianzhiMap = Maps.newHashMap();
        for (UserNameInfo u : bianzhiList) {
            bianzhiMap.put(u.getName(), u.getInfo());
        }
        //工行卡号
        List<Object> icbc = getExcelData("D:\\work\\工资系统\\初始化工资数据\\李燕妮\\内聘的工行卡号.xls", "内聘的工行卡号.xls");
        Object obj2 = (Object) icbc;
        List<UserNameInfo> icbcList = (List<UserNameInfo>) obj2;
        HashMap<String, String> icbcMap = Maps.newHashMap();
        for (UserNameInfo u : icbcList) {
            icbcMap.put(u.getName(), u.getInfo());
        }
        //身份证信息
        List<Object> datas = getExcelData();
        Object obj = (Object) datas;
        List<User> list = (List<User>) obj;
        //处理
        Set<String> set = Sets.newHashSet();
        for (User user : list) {
            String num = user.getNum();
            String name = user.getName();
            String idNum = user.getIdNum();
            String mobile = user.getMobile();
            isExist(set, num);
            isExist(set, name);
            isExist(set, idNum);
            isExist(set, mobile);
        }
        //准备存入db
        List<SysUser> sysUserList = Lists.newArrayList();
        for (User user : list) {
            SysUser sysUser = new SysUser();
            sysUser.setName(user.getName());
            //身份证号后6位
            String idNum6 = idNum(user.getIdNum());
            sysUser.setPwd(Md5Util.encryPassword(idNum6));

            sysUser.setNum(user.getNum());
            sysUser.setCategory(bianzhiMap.get(user.getName()));
            sysUser.setIdNum(user.getIdNum());
            sysUser.setMobile(user.getMobile());
            sysUser.setBankAccount(icbcMap.get(user.getName()));
            sysUser.setJob("在职");
            sysUserList.add(sysUser);
            sysUser.setOrg("航天海鹰安全");
            //部门
            SysDept sysDept = SysDeptService.getOne(new QueryWrapper<SysDept>().eq("name", user.getDeptName()));
            sysUser.setDeptId(sysDept.getId());
            sysUser.setDeptName(sysDept.getName());
        }
        flag = SysUserService.saveBatch(sysUserList);
        return flag;
    }

    @ApiOperation("退休用户数据")
    @GetMapping("/tx_user")
    public boolean tx_user() throws Exception {
        boolean flag = true;
        //姓名
        List<Object> sall = getExcelData("D:\\work\\工资系统\\初始化工资数据\\离退.xlsx", "离退.xlsx", NpSalMoney.class);
        Object objj = (Object) sall;
        List<NpSalMoney> salListt = (List<NpSalMoney>) objj;
        //编制
        List<Object> bianzhi = getExcelData("D:\\work\\工资系统\\初始化工资数据\\沈亚超\\退休的编制.xlsx", "退休的编制.xlsx");
        Object obj1 = (Object) bianzhi;
        List<UserNameInfo> bianzhiList = (List<UserNameInfo>) obj1;
        HashMap<String, String> bianzhiMap = Maps.newHashMap();
        for (UserNameInfo u : bianzhiList) {
            bianzhiMap.put(u.getName(), u.getInfo());
        }
        //工行卡号
        List<Object> icbc = getExcelData("D:\\work\\工资系统\\初始化工资数据\\李燕妮\\退休的工行卡号.xlsx", "退休的工行卡号.xlsx");
        Object obj2 = (Object) icbc;
        List<UserNameInfo> icbcList = (List<UserNameInfo>) obj2;
        HashMap<String, String> icbcMap = Maps.newHashMap();
        for (UserNameInfo u : icbcList) {
            icbcMap.put(u.getName().trim(), u.getInfo().trim());
        }
        //准备存入db
        List<SysUser> sysUserList = Lists.newArrayList();
        int num = 1;
        for (NpSalMoney user : salListt) {
            String name = user.getName();
            SysUser sysUser = new SysUser();
            sysUser.setName(name);
            sysUser.setNum("无" + num++);
            sysUser.setPwd(Md5Util.encryPassword("123"));
            sysUser.setCategory(bianzhiMap.get(name));
            sysUser.setBankAccount(icbcMap.get(name));
            sysUser.setJob("不在职之退休");
            sysUser.setOrg("航天海鹰安全");
            //部门
            sysUser.setDeptId(80);
            sysUser.setDeptName("其他");
            if (!Strings.isNullOrEmpty(name)) {
                sysUserList.add(sysUser);
            }
        }
        flag = SysUserService.saveBatch(sysUserList);
        return flag;
    }

    public List<Object> getExcelData(String path, String fileName) throws Exception {
        //准备参数
        InputStream inputStream = new FileInputStream(path);
        Class<? extends BaseRowModel> objClass = UserNameInfo.class;
        List<Object> datas = Lists.newArrayList();
        //读取数据
        ExcelUtil.readExcel(inputStream, fileName, objClass, datas);
        return datas;
    }

    public List<Object> getExcelData(String path, String fileName, Class clazz) throws Exception {
        //准备参数
        InputStream inputStream = new FileInputStream(path);
        Class<? extends BaseRowModel> objClass = clazz;
        List<Object> datas = Lists.newArrayList();
        //读取数据
        ExcelUtil.readExcel(inputStream, fileName, objClass, datas);
        return datas;
    }

    public List<Object> getExcelData() throws Exception {
        //准备参数
        InputStream inputStream = new FileInputStream("D:\\work\\工资系统\\初始化工资数据\\李燕妮\\内聘的身份证电话信息.xls");
        String fileName = "内聘的身份证电话信息.xls";
        Class<? extends BaseRowModel> objClass = User.class;
        List<Object> datas = Lists.newArrayList();
        //读取数据
        ExcelUtil.readExcel(inputStream, fileName, objClass, datas);
        return datas;
    }

    public boolean isExist(Set<String> set, String value) {
        if (set.contains(value)) {
            throw new RuntimeException("有重复数据");
        }
        return false;
    }

    //取身份证号后6位
    private String idNum(String id) {
        StringBuilder sb = new StringBuilder();
        String[] ids = id.split("");
        for (int i = ids.length - 1; i >= ids.length - 1 - 5; i--) {
            sb.append(ids[i]);
        }
        return sb.reverse().toString();
    }


    private Double yingkouSum(SalLtx d) {
        return ofNullable(d.getFangzu()).orElse(0.0) +
                ofNullable(d.getYingkouqita()).orElse(0.0);
    }

    private Double yingfaSum(SalLtx d) {
        return ofNullable(d.getJiben()).orElse(0.0) +
                ofNullable(d.getGuifan()).orElse(0.0) +
                ofNullable(d.getBaoliu()).orElse(0.0) +
                ofNullable(d.getButie()).orElse(0.0) +
                ofNullable(d.getShubao()).orElse(0.0) +
                ofNullable(d.getTizu()).orElse(0.0) +
                ofNullable(d.getTiao()).orElse(0.0) +
                ofNullable(d.getBucha()).orElse(0.0) +
                ofNullable(d.getZengzi()).orElse(0.0) +
                ofNullable(d.getYingfaqita()).orElse(0.0);
    }
}
