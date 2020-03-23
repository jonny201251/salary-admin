package com.hthyaq.salaryadmin.util;

import com.google.common.collect.Maps;

import java.time.LocalDateTime;
import java.util.Map;

public class YearMonth {
    public static LocalDateTime localDateTime = LocalDateTime.now();

    public static Integer getCurrentYear() {
        return localDateTime.getYear();
    }

    public static Integer getCurrentMonth() {
        return localDateTime.getMonth().getValue();
    }

    /***
     *月结时，可以在当月点击，也可以在下个月里点击
     * 当前的年份    当前的月份   工资表的年份  工资表的月份  是否可以月结  当前年份和月份     大小关系     工资表的年份和月份
     * 2018             12          2018        12              是           201812                  =        201812
     * 2018             12          2019        1               否           201812                  <        201901
     * 2019             1           2018        12              是           201901                  >        201812
     * 2019             1           2019        1               是           201901                  =        201901
     * 2019             1           2019        2               否           201901                  <        201902
     * 2019             2           2019        1               是           201902                  >        201901
     * 月结的条件：
     *  当前年份和月份 >= 工资表的年份和月份
     */
    //判断是否可以月结
    public static boolean isFinish(Integer salYear, Integer salMonth) {
        Integer currentYearMonthInt = getYearMonthInt();
        Integer salYearMonthInt = getYearMonthInt(salYear, salMonth);
        if (currentYearMonthInt >= salYearMonthInt) {
            return true;
        }else{
            throw new RuntimeException("月结时，已经超过了3个月，请调整时间！");
        }
    }

    //获取传递过来日期的上个月的年份和月份,如2019年01月、2019年1月
    public static Map<String, Integer> getLast(String yearmonthString) {
        String[] yearmonth = yearmonthString.split("年|月");
        Integer year = Integer.parseInt(yearmonth[0]);
        Integer month = Integer.parseInt(yearmonth[1]);
        return getLast(year, month);
    }

    //获取当前系统的上个月的年份和月份
    public static Map<String, Integer> getLast() {
        return getLast(getCurrentYear(), getCurrentMonth());
    }

    //获取给定年份和月份的上个月的年份和月份
    public static Map<String, Integer> getLast(Integer year, Integer month) {
        Map<String, Integer> hm = Maps.newHashMap();
        if (month == 1) {
            hm.put("lastYear", year - 1);
            hm.put("lastMonth", 12);
        } else {
            hm.put("lastYear", year);
            hm.put("lastMonth", month - 1);
        }
        return hm;
    }

    //获取当前系统的下个月的年份和月份
    public static Map<String, Integer> getNext() {
        return getNext(getCurrentYear(), getCurrentMonth());
    }

    //获取给定年份和月份的下个月的年份和月份
    public static Map<String, Integer> getNext(Integer year, Integer month) {
        Map<String, Integer> hm = Maps.newHashMap();
        if (month == 12) {
            hm.put("nextYear", year + 1);
            hm.put("nextMonth", 1);
        } else {
            hm.put("nextYear", year);
            hm.put("nextMonth", month + 1);
        }
        return hm;
    }

    //获取传递过来日期的下个月的年份和月份
    public static Map<String, Integer> getNext(String yearmonthString) {
        String[] yearmonth = yearmonthString.split("年|月");
        Integer year = Integer.parseInt(yearmonth[0]);
        Integer month = Integer.parseInt(yearmonth[1]);
        return getNext(year, month);
    }

    //根据页面传递过来的yearmonth: "2019年01月,2019年01月",取出开始和结束日期
    public static Map<String, Integer> getStartEndYearMonth(String yearmonth) {
        Map<String, Integer> map = Maps.newHashMap();
        String[] yearmonths = yearmonth.split(",");
        map.put("start", getYearMonthInt(yearmonths[0]));
        map.put("end", getYearMonthInt(yearmonths[1]));
        return map;
    }

    //处理2019年01月、2019年1月的日期
    public static Map<String, Integer> getYearMonth(String yearmonthString) {
        Map<String, Integer> map = Maps.newHashMap();
        String[] yearmonth = yearmonthString.split("年|月");
        map.put(Constants.YEAR, Integer.parseInt(yearmonth[0]));
        map.put(Constants.MONTH, Integer.parseInt(yearmonth[1]));
        Integer yearMonthInt = getYearMonthInt(yearmonthString);
        map.put(Constants.YEAR_MONTH_INT, yearMonthInt);
        return map;
    }

    //处理2019年01月、2019年1月的日期-->201901
    public static Integer getYearMonthInt(String yearmonthString) {
        String[] yearmonth = yearmonthString.split("年|月");
        String month = yearmonth[1];
        if (month.length() == 1) {
            return Integer.parseInt(yearmonth[0] + "0" + yearmonth[1]);
        } else {
            return Integer.parseInt(yearmonth[0] + yearmonth[1]);
        }
    }

    //2019,1-->201901
    public static Integer getYearMonthInt(String year, String month) {
        if (month.length() == 1) {
            return Integer.parseInt(year + "0" + month);
        } else {
            return Integer.parseInt(year + month);
        }
    }

    //2019,1-->201901
    public static Integer getYearMonthInt(Integer year, Integer month) {
        return getYearMonthInt(year + "", month + "");
    }

    //获取当前月份的yearMonthInt
    public static Integer getYearMonthInt() {
        return getYearMonthInt(getCurrentYear(), getCurrentMonth());
    }

    //获取当前月份的yearMonthString
    public static String getYearMonthString() {
        return getCurrentYear()+"年"+getCurrentMonth()+"月";
    }
}
