package com.github.cbyzzy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Title 日期工具类
 */
public class DateUtils {
    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {
    }

    /**
     * @param ms
     */
    public static String milliSecondFormat(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        // 天
        if (ms >= dd) {
            return ms / dd + "天";
        }

        // 小时
        if (ms >= hh) {
            return ms / hh + "小时";
        }

        // 分钟
        if (ms >= mi) {
            return ms / mi + "分钟";
        }

        // 秒
        if (ms >= ss) {
            return ms / ss + "秒";
        }

        // 毫秒不显示
        if (ms < ss) {
            return "0秒";
        }
        return "";
    }

    /**
     * 将毫秒数换算成x天x时x分x秒x毫秒
     *
     * @param ms   毫秒
     * @param type 1.完整的时间 2.到时、分、秒 3.到时、分、秒、毫秒 默认为.到分、秒
     * @return 时间
     */
    public static String milliSecondFormat(long ms, String type) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : Long.toString(day);
        String strHour = hour < 10 ? "0" + hour : Long.toString(hour);
        String strMinute = minute < 10 ? "0" + minute : Long.toString(minute);
        String strSecond = second < 10 ? "0" + second : Long.toString(second);
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : Long.toString(milliSecond);
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : strMilliSecond;

        String time;
        switch (Integer.valueOf(type)) {
            case 1:
                time = strDay + "-" + strHour + ":" + strMinute + ":" + strSecond + "-" + strMilliSecond;
                break;
            case 2:
                time = strHour + ":" + strMinute + ":" + strSecond;
                break;
            case 3:
                time = strHour + ":" + strMinute + ":" + strSecond + " " + strMilliSecond;
                break;
            default:
                time = strMinute + ":" + strSecond;
        }
        return time;
    }

    /**
     * @param str
     */
    public static Date toDate(String str, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(str);
    }

    /**
     * Description：时间转字符串
     * @return
     */
    public static String toString(Date date, String format) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 判断target是否在start和end之间
     *
     * @param target
     * @param start
     * @param end
     * @return
     */
    public static boolean between(Date target, Date start, Date end) {
        Calendar starCal = Calendar.getInstance();
        starCal.setTime(start);
        starCal.set(Calendar.HOUR_OF_DAY, 0);
        starCal.set(Calendar.MINUTE, 0);
        starCal.set(Calendar.SECOND, 0);
        Date startTime = starCal.getTime();
        starCal.setTime(end);
        starCal.set(Calendar.HOUR_OF_DAY, 23);
        starCal.set(Calendar.MINUTE, 59);
        starCal.set(Calendar.SECOND, 59);
        Date endTime = starCal.getTime();

        return target.getTime() - startTime.getTime() >= 0 && target.getTime() - endTime.getTime() <= 0;
    }

    /**
     * @return
     * @Description 获取当年第一天
     */
    public static Date startOfYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return startOfYear(year);
    }

    /**
     * @return
     * @Description 指定年份第一天
     */
    public static Date startOfYear(Integer year) {
        if (year == null) {
            return startOfYear();
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 获取当年最后一天
     */
    public static Date endOfYear() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        return endOfYear(year);
    }

    /**
     * @return
     * @Description 指定年份最后一天
     */
    public static Date endOfYear(Integer year) {
        if (year == null) {
            return endOfYear();
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 当月第一天
     */
    public static Date startOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 上个月第一天
     *
     * @return
     */
    public static Date startOfLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startOfMonth());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 当月最后一天
     */
    public static Date endOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.SECOND, -1);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 指定月份最后一天
     */
    public static Date endOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.SECOND, -1);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 指定月份第一天
     */
    public static Date startOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 当天开始时间
     */
    public static Date startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 当天结束时间
     */
    public static Date endOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * @return
     * @Description 指定日期当天结束时间
     */
    public static Date endOfToday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * @param date
     * @return
     * @Description 判断是否为周末，周六和周日返回true
     */
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return week == 6 || week == 0;
    }

    public static LocalDate toLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 修改年份数值
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date addYear(Date date, int amount) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.YEAR, amount);
        return instance.getTime();
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static Integer thisYear() {
        return LocalDate.now().getYear();
    }


    /**
     * 获取时间段内的所有月份节点
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static LinkedHashMap<String, Object> timeNodes(Date startDate, Date endDate) {
        LinkedHashMap<String, Object> linkMap = new LinkedHashMap<>();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        if (startDate == null) {
            start.setTime(DateUtils.startOfYear());
        } else {
            start.setTime(startDate);
        }
        if (endDate == null) {
            end.setTime(new Date());
        } else {
            end.setTime(endDate);
        }
        while (start.compareTo(end) <= 0) {
            String startStr = DateUtils.toString(start.getTime(), DateUtils.YYYY_MM);
            start.add(Calendar.MONTH, 1);
            linkMap.put(startStr, new Object());

        }
        return linkMap;
    }

    /**
     * 将LinkedHashMap 的key 存在list中
     * @param map
     * @return
     */
    public static List<String> toList(LinkedHashMap<String,Object> map){
        List<String> timeNodes = new ArrayList<>();
        for (String key:map.keySet()){
            timeNodes.add(key);
        }
        return timeNodes;
    }

    /**
     * LocalDate 转 Date
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 转 Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
