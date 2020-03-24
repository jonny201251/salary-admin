package com.hthyaq.salaryadmin.util;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

public class Constants {
    //分页时，显示的数据条数
    public static final Integer PAGE_SIZE = 10;
    public static final Boolean TRUE = true;
    public static final Boolean FALSE = false;
    //发放方式=站发、院发
    public static final String GIVE_MODE_ZHAN = "站发";
    public static final String GIVE_MODE_YUAN = "院发";
    //工资计算时的数据的操作,页面、应发应扣、月结、其他薪金、计税专用项
    public static final String SALARY_CALCULATE_TYPE_PAGE = "页面";
    public static final String SALARY_CALCULATE_TYPE_YINGFAYINGKOU = "应发应扣";
    public static final String SALARY_CALCULATE_TYPE_YUEJIE = "月结";
    public static final String SALARY_CALCULATE_TYPE_OTHER = "其他薪金";
    public static final String SALARY_CALCULATE_TYPE_TAX = "计税专用项";
    //税款1、税款2
    public static final String SHUIKUAN1 = "税款1";
    public static final String SHUIKUAN2 = "税款2";
    //状态码
    public static final Integer SUCCESS = 1;
    public static final String SUCCESS_MSG = "操作成功！";
    public static final Integer FAIL = 2;
    public static final String FAIL_MSG = "操作失败！";
    //换行符
    public static final String NEWLINE = System.getProperty("line.separator");
    //路径分隔符
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    //应发、应扣
    public static final String YING_FA = "应发";
    public static final String YING_KOU = "应扣";
    //内聘、退休、离休
    public static final String SAL_NP = "sal_np";
    public static final String SAL_LTX = "sal_ltx";
    public static final String SAL_LX = "sal_lx";
    //应发计税、应发不计税、all
    public static final String YINGFA_TAX = "应发计税";
    public static final String YINGFA_NO_TAX = "应发不计税";
    public static final String YINGFA_ALL = "all";
    //内聘、离退休、离休的路径标志
    public static final String PATH_SAL_NP = "/salNp";
    public static final String PATH_SAL_LTX = "/salLtx";
    public static final String PATH_SAL_LX = "/salLx";
    //计税专用项：加项、减项
    public static final String ADD = "加项";
    public static final String SUBTRACT = "减项";
    //人员变动单-操作类型
    public static final String USER_ADD = "新增人员";
    public static final String USER_CHANGE = "人员部门调动";
    public static final String USER_JOB = "在职";
    public static final String USER_NOT_JOB_RETIRE = "不在职之退休";
    public static final String USER_NOT_JOB_GO = "不在职之调出";
    public static final String USER_NOT_JOB_LEAVE = "不在职之离职";
    public static final String USER_NOT_JOB_DIE = "不在职之死亡";
    public static final String USER_NOT_JOB_LX = "不在职之离休";
    public static final String USER_LTX_DIE = "离退休之死亡";
    //部门变动单-操作类型
    public static final String DEPT_CHANGE = "名称变更";
    public static final String DEPT_RECOMBINE = "部门重组";
    //使用状态
    public static final String NORMAL = "正常";
    public static final String DISABLE = "禁用";
    //页面按钮
    public static final String CREATE = "create";
    public static final String EDIT = "edit";
    //权限类型
    public static final String TYPE_NAV = "导航菜单";
    public static final String TYPE_BUTTON = "操作按钮";
    //登录错误提示
    public static final String LOGIN_FAIL_INFO = "登录名或密码错误";
    //流程状态的结束标志
    public static final String PROCESS_END = "已完成审批";
    //待处理的变动单的菜单的按钮,生成、退回处理、校对、确认、审批
    public static final String BUTTON_TYPE_GENERATE = "generate";
    public static final String BUTTON_TYPE_BACK = "back";
    public static final String BUTTON_TYPE_VALIDATE = "validate";
    public static final String BUTTON_TYPE_CONFIRM = "confirm";
    public static final String BUTTON_TYPE_AUDITING = "auditing";
    //提交给人事领导
    public static final String COMMIT_TO_HUMAN_LEADER = "提交给人事领导";
    //excel文件的目录
    public static String EXCEL_PAHT = "d:/salaryFile/excel/";
    //ureport文件的目录
    public static final String UREPORT_PAHT = "d:/salaryFile/ureportfiles";
    //临时文件的目录
    public static final String TMP_PAHT = "d:/salaryFile/tmp";

    static {
        File UREPORT_PAHT_File = new File(UREPORT_PAHT);
        if (!UREPORT_PAHT_File.exists()) UREPORT_PAHT_File.mkdirs();

        File TMP_PAHT_File = new File(TMP_PAHT);
        if (!TMP_PAHT_File.exists()) TMP_PAHT_File.mkdirs();
    }

    //上传多个excel完成批量修改的类别
    //应发应扣
    public static final String BATCH_MODIFY_YINGFA = "应发应扣";
    //其他薪金
    public static final String BATCH_MODIFY_OTHER_BONUS = "其他薪金";
    //计税专用-加项
    public static final String BATCH_MODIFY_TAX_ADD = "计税专用-加项";
    //计税专用-减项
    public static final String BATCH_MODIFY_TAX_SUBSTRACT = "计税专用-减项";
    //计税专用
//    public static final List<String> TAX_COLUMNS = Lists.newArrayList("食补", "基本扣除项", "累计子女教育支出扣除", "累计赡养老人支出扣除", "累计继续教育支出扣除", "累计住房贷款利息支出扣除", "累计住房租金支出扣除", "商业健康保险");
    public static final List<String> TAX_COLUMNS = Lists.newArrayList("食补", "基本扣除项");

    //月结状态
    public static final String FINISH_STATUS_NO = "未月结";
    public static final String FINISH_STATUS_YES = "已月结";
    //年份、月份
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String YEAR_MONTH_INT = "yearMonthInt";
    public static final String LAST_YEAR = "lastYear";
    public static final String LAST_MONTH = "lastMonth";
    public static final String NEXT_YEAR = "nextYear";
    public static final String NEXT_MONTH = "nextMonth";

}
