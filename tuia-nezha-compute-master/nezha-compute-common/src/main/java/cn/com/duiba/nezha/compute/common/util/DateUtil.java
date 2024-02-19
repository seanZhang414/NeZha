package cn.com.duiba.nezha.compute.common.util;


import cn.com.duiba.nezha.compute.common.enums.DateStyle;
import cn.com.duiba.nezha.compute.common.enums.Week;
import org.apache.commons.collections.map.HashedMap;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by pc on 2017/1/10.
 */
public class DateUtil {

    public static Date getCurrentDate() {
        Date date = new Date();
        return date;
    }


    public static String getCurrentTime(DateStyle partition) {
        return getTimeString(getCurrentDate(),partition);
    }


    public static String getCurrentTime() {
        return getCurrentTime(DateStyle.YYYY_MM_DD_HH_MM_SS);
    }


    public static String getTimeString(Date date, DateStyle partition) {
        SimpleDateFormat sdf = new SimpleDateFormat(partition.getValue());
        return sdf.format(date);
    }


    public static String getTimeString(Date date) {
        return getTimeString(date, DateStyle.YYYY_MM_DD_HH_MM_SS);
    }



    /**
     * 获取SimpleDateFormat
     *
     * @param parttern 日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException 异常：非法日期格式
     */
    private static SimpleDateFormat getDateFormat(String parttern) throws RuntimeException {
        if (parttern == null) {
            return new SimpleDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue());
        }
        return new SimpleDateFormat(parttern);
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date     日期
     * @param dateType 日期格式
     * @return 数值
     */
    private static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(dateType);
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期字符串
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期字符串
     */
    private static String addInteger(String date, int dateType, int amount) {
        String dateString = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            myDate = addInteger(myDate, dateType, amount);
            dateString = DateToString(myDate, dateStyle);
        }
        return dateString;
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期
     */
    private static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }

    /**
     * 获取精确的日期
     *
     * @param timestamps 时间long集合
     * @return 日期
     */
    private static Date getAccurateDate(List<Long> timestamps) {
        Date date = null;
        long timestamp = 0;
        Map<Long, long[]> map = new HashMap<>();
        List<Long> absoluteValues = new ArrayList<>();

        if (timestamps != null && timestamps.size() > 0) {
            if (timestamps.size() > 1) {
                for (int i = 0; i < timestamps.size(); i++) {
                    for (int j = i + 1; j < timestamps.size(); j++) {
                        long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
                        absoluteValues.add(absoluteValue);
                        long[] timestampTmp = {timestamps.get(i), timestamps.get(j)};
                        map.put(absoluteValue, timestampTmp);
                    }
                }

                // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的
                long minAbsoluteValue = -1;
                if (!absoluteValues.isEmpty()) {
                    // 如果timestamps的size为2，这是差值只有一个，因此要给默认值
                    minAbsoluteValue = absoluteValues.get(0);
                }
                for (int i = 0; i < absoluteValues.size(); i++) {
                    for (int j = i + 1; j < absoluteValues.size(); j++) {
                        if (absoluteValues.get(i) > absoluteValues.get(j)) {
                            minAbsoluteValue = absoluteValues.get(j);
                        } else {
                            minAbsoluteValue = absoluteValues.get(i);
                        }
                    }
                }

                if (minAbsoluteValue != -1) {
                    long[] timestampsLastTmp = map.get(minAbsoluteValue);
                    if (absoluteValues.size() > 1) {
                        timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
                    } else if (absoluteValues.size() == 1) {
                        // 当timestamps的size为2，需要与当前时间作为参照
                        long dateOne = timestampsLastTmp[0];
                        long dateTwo = timestampsLastTmp[1];
                        if ((Math.abs(dateOne - dateTwo)) < 100000000000L) {
                            timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
                        } else {
                            long now = new Date().getTime();
                            if (Math.abs(dateOne - now) <= Math.abs(dateTwo - now)) {
                                timestamp = dateOne;
                            } else {
                                timestamp = dateTwo;
                            }
                        }
                    }
                }
            } else {
                timestamp = timestamps.get(0);
            }
        }

        if (timestamp != 0) {
            date = new Date(timestamp);
        }
        return date;
    }

    /**
     * 判断字符串是否为日期字符串
     *
     * @param date 日期字符串
     * @return true or false
     */
    public static boolean isDate(String date) {
        boolean isDate = false;
        if (date != null) {
            if (StringToDate(date) != null) {
                isDate = true;
            }
        }
        return isDate;
    }

    /**
     * 获取日期字符串的日期风格。失敗返回null。
     *
     * @param date 日期字符串
     * @return 日期风格
     */
    public static DateStyle getDateStyle(String date) {
        DateStyle dateStyle = null;
        Map<Long, DateStyle> map = new HashMap<>();
        List<Long> timestamps = new ArrayList<>();
        for (DateStyle style : DateStyle.values()) {
            Date dateTmp = StringToDate(date, style.getValue());
            if (dateTmp != null) {
                timestamps.add(dateTmp.getTime());
                map.put(dateTmp.getTime(), style);
            }
        }

        Date retDate = getAccurateDate(timestamps);
        if (retDate != null) {
            dateStyle = map.get(retDate.getTime());
        }
        return dateStyle;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date StringToDate(String date) {
        DateStyle dateStyle = null;
        return StringToDate(date, dateStyle);
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date     日期字符串
     * @param parttern 日期格式
     * @return 日期
     */
    public static Date StringToDate(String date, String parttern) {
        Date myDate = null;
        if (date != null) {
            try {
                myDate = getDateFormat(parttern).parse(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return myDate;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date      日期字符串
     * @param dateStyle 日期风格
     * @return 日期
     */
    public static Date StringToDate(String date, DateStyle dateStyle) {

        return StringToDate(date, dateStyle.getValue());
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date     日期
     * @param parttern 日期格式
     * @return 日期字符串
     */
    public static String DateToString(Date date, String parttern) {
        String dateString = null;
        if (date != null) {
            try {
                dateString = getDateFormat(parttern).format(date);
            } catch (Exception e) {
            }
        }
        return dateString;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date      日期
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String DateToString(Date date, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {
            dateString = DateToString(date, dateStyle.getValue());
        }
        return dateString;
    }


    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date         旧日期字符串
     * @param olddParttern 旧日期格式
     * @param newParttern  新日期格式
     * @return 新日期字符串
     */
    public static String StringToString(String date, String olddParttern, String newParttern) {
        String dateString = null;
        if (olddParttern == null) {
            DateStyle style = getDateStyle(date);
            if (style != null) {
                Date myDate = StringToDate(date, style.getValue());
                dateString = DateToString(myDate, newParttern);
            }
        } else {
            Date myDate = StringToDate(date, olddParttern);
            dateString = DateToString(myDate, newParttern);
        }
        return dateString;
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date         旧日期字符串
     * @param olddDteStyle 旧日期风格
     * @param newDateStyle 新日期风格
     * @return 新日期字符串
     */
    public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
        return StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期字符串
     */
    public static String addYear(String date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期
     */
    public static Date addYear(Date date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    public static String addMonth(String date, int yearAmount) {
        return addInteger(date, Calendar.MONTH, yearAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加月份后的日期
     */
    public static Date addMonth(Date date, int yearAmount) {
        return addInteger(date, Calendar.MONTH, yearAmount);
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date      日期字符串
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期字符串
     */
    public static String addDay(String date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期
     */
    public static Date addDay(Date date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date       日期字符串
     * @param hourAmount 增加数量。可为负数
     * @return 增加小时后的日期字符串
     */
    public static String addHour(String date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date       日期
     * @param hourAmount 增加数量。可为负数
     * @return 增加小时后的日期
     */
    public static Date addHour(Date date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date       日期字符串
     * @param hourAmount 增加数量。可为负数
     * @return 增加分钟后的日期字符串
     */
    public static String addMinute(String date, int hourAmount) {
        return addInteger(date, Calendar.MINUTE, hourAmount);
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date       日期
     * @param hourAmount 增加数量。可为负数
     * @return 增加分钟后的日期
     */
    public static Date addMinute(Date date, int hourAmount) {
        return addInteger(date, Calendar.MINUTE, hourAmount);
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date       日期字符串
     * @param hourAmount 增加数量。可为负数
     * @return 增加秒钟后的日期字符串
     */
    public static String addSecond(String date, int hourAmount) {
        return addInteger(date, Calendar.SECOND, hourAmount);
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date       日期
     * @param hourAmount 增加数量。可为负数
     * @return 增加秒钟后的日期
     */
    public static Date addSecond(Date date, int hourAmount) {
        return addInteger(date, Calendar.SECOND, hourAmount);
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期字符串
     * @return 年份
     */
    public static Integer getYear(String date) {
        Integer year = null;

        DateStyle dateStyle = getDateStyle(date);

        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            year = getYear(myDate);
        }

        return year;

    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期字符串
     * @return 月份
     */
    public static Integer getMonth(String date) {
        Integer month = null;

        DateStyle dateStyle = getDateStyle(date);

        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            month = getMonth(myDate);
        }

        return month;
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    public static Integer getMonth(Date date) {
        return getInteger(date, Calendar.MONTH);
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期字符串
     * @return 天
     */
    public static Integer getDay(String date) {

        Integer day = null;

        DateStyle dateStyle = getDateStyle(date);

        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            day = getDay(myDate);
        }

        return day;

    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期
     * @return 天
     */
    public static Integer getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }


    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期字符串
     * @return 小时
     */
    public static Integer getHour(String date, DateStyle dateStyle) {

        Integer hour = null;
        try {
            Date myDate = StringToDate(date, dateStyle);
            hour = getHour(myDate);
        } catch (Exception e) {

        }
        return hour;
    }


    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期
     * @return 小时
     */
    public static Integer getHour(Date date) {
        return getInteger(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期字符串
     * @return 分钟
     */
    public static Integer getMinute(String date, DateStyle dateStyle) {

        Integer minute = null;
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            minute = getMinute(myDate);
        }

        return minute;
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期
     * @return 分钟
     */
    public static Integer getMinute(Date date) {
        return getInteger(date, Calendar.MINUTE);
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期字符串
     * @return 秒钟
     */
    public static Integer getSecond(String date, DateStyle dateStyle) {

        Integer second = null;


        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            second = getSecond(myDate);
        }

        return second;

    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期
     * @return 秒钟
     */
    public static Integer getSecond(Date date) {
        return getInteger(date, Calendar.SECOND);
    }



    /**
     * 获取日期。默认yyyy-MM-dd格式。失败返回null。
     *
     * @param date 日期
     * @return 日期
     */
    public static String getDate(Date date, DateStyle dateStyle) {
        return DateToString(date, dateStyle);
    }

    public static String getDate(String date) {

        return  getDaySubTimeString(date, DateStyle.YYYY_MM_DD_HH_MM_SS, DateStyle.YYYY_MM_DD, 0);
    }




    public static Date getDateTime(String date) {
        Date ret = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            ret = StringToDate(date, dateStyle);
        }
        return ret;
    }

    public static Date getDate(String date, DateStyle dateStyle) {
        Date ret = null;


        ret = StringToDate(date, dateStyle);
        return ret;
    }


    public static String getDateTime(Date date, DateStyle dateStyle) {
        return DateToString(date, dateStyle);
    }



    /**
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
     *
     * @param date 日期
     * @return 时间
     */
    public static String getTime(Date date) {
        return DateToString(date, DateStyle.HH_MM_SS);
    }


    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期字符串
     * @return 星期
     */
    public static Week getWeek(String date) {
        Week week = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            week = getWeek(myDate);
        }
        return week;
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期
     * @return 星期
     */
    public static Week getWeek(Date date) {
        Week week = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (weekNumber) {
            case 0:
                week = Week.SUNDAY;
                break;
            case 1:
                week = Week.MONDAY;
                break;
            case 2:
                week = Week.TUESDAY;
                break;
            case 3:
                week = Week.WEDNESDAY;
                break;
            case 4:
                week = Week.THURSDAY;
                break;
            case 5:
                week = Week.FRIDAY;
                break;
            case 6:
                week = Week.SATURDAY;
                break;
        }
        return week;
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期字符串
     * @return 星期
     */
    public static Integer getWeekNumber(String date, DateStyle dateStyle) {
        Integer weekNumber = null;
        try {
            Date myDate = StringToDate(date, dateStyle);
            weekNumber = getWeekNumber(myDate);
        } catch (Exception e) {
        }
        return weekNumber;
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期字符串
     * @return 星期
     */
    public static Integer getWeekNumber(String date) {
        Integer weekNumber = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            weekNumber = getWeekNumber(myDate);
        }
        return weekNumber;
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期
     * @return 星期
     */
    public static Integer getWeekNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekNumber;
    }


    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差天数
     */
    public static Integer getIntervalDays(String date, String otherDate) {
        return getIntervalDays(StringToDate(date), StringToDate(otherDate));
    }

    /**
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差天数
     */
    public static Integer getIntervalDays(Date date, Date otherDate) {

        if (date == null || otherDate == null) {
            return null;
        }
        date = DateUtil.StringToDate(DateUtil.getDate(date, DateStyle.YYYY_MM_DD));
        long time = Math.abs(date.getTime() - otherDate.getTime());
        return (int) (time / (24 * 60 * 60 * 1000));
    }


    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差小时数
     */
    public static Integer getIntervalHours(String date, String otherDate) {

        Integer hours = null;

        DateStyle dateStyle = getDateStyle(date);
        DateStyle otherDateStyle = getDateStyle(otherDate);
        if (dateStyle != null && otherDateStyle != null) {
            hours = getIntervalHours(StringToDate(date, dateStyle), StringToDate(otherDate, otherDateStyle));
        }
        return hours;
    }

    /**
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差小时数
     */
    public static Integer getIntervalHours(Date date, Date otherDate) {
        long time = Math.abs(date.getTime() - otherDate.getTime());
        return (int) (time / (60 * 60 * 1000));
    }

    /**
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差小时数
     */
    public static Integer getIntervalYears(Date date, Date otherDate) {
        date = DateUtil.StringToDate(DateUtil.getDate(date, DateStyle.YYYY_MM_DD));
        long time = Math.abs(date.getTime() - otherDate.getTime());

        return (int) (time / (24 * 60 * 60 * 1000) / 365);
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差小时数
     */
    public static Integer getIntervalYears(String date, String otherDate) {

        Integer years = null;

        DateStyle dateStyle = getDateStyle(date);
        DateStyle otherDateStyle = getDateStyle(otherDate);
        if (dateStyle != null && otherDateStyle != null) {
            years = getIntervalYears(StringToDate(date, dateStyle), StringToDate(otherDate, otherDateStyle));
        }
        return years;
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差小时数
     */
    public static Integer getIntervalMinutes(String date, String otherDate) {

        Integer hours = null;

        DateStyle dateStyle = getDateStyle(date);
        DateStyle otherDateStyle = getDateStyle(otherDate);
        if (dateStyle != null && otherDateStyle != null) {
            hours = getIntervalMinutes(StringToDate(date, dateStyle), StringToDate(otherDate, otherDateStyle));
        }
        return hours;
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差小时数
     */
    public static Integer getIntervalMinutes(String date, String otherDate, DateStyle dateStyle) {

        Integer hours = null;
        if(date!=null && otherDate!=null && dateStyle!=null){
            hours = getIntervalMinutes(StringToDate(date, dateStyle), StringToDate(otherDate, dateStyle));
        }
        return hours;
    }


    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差小时数
     */
    public static Integer getIntervalMinutes(String date, String otherDate, DateStyle dateStyle, DateStyle otherDateStyle) {

        Integer ret = null;
        try {
            ret = getIntervalMinutes(StringToDate(date, dateStyle), StringToDate(otherDate, otherDateStyle));
        } catch (Exception e) {

        }

        return ret;
    }


    /**
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 相差小时数
     */
    public static Integer getIntervalMinutes(Date date, Date otherDate) {
        Integer ret =null;
        if(date!=null && otherDate!=null){
            long time = Math.abs(date.getTime() - otherDate.getTime());
            ret = (int) (time / (60 * 1000));
        }
        return ret;
    }


    /**
     * @param dateStr
     * @param dayInterval
     * @return
     */
    public static String getDaySubTimeString(String dateStr, DateStyle dateStyle, DateStyle retDateStyle, int dayInterval) {
        String ret = null;
        try {

            Date otherDate = getSubDay(dateStr, dateStyle, dayInterval);
            ret = getDateTime(otherDate, retDateStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    /**
     * @param dateStr
     * @param dayInterval
     * @return
     */
    public static Date getSubDay(String dateStr, DateStyle dateStyle, int dayInterval) {
        Date date = StringToDate(dateStr, dateStyle);

        Date ret = getSubDay(date, dayInterval);
        return ret;
    }

    /**
     * @param date
     * @param dayInterval
     * @return
     */
    public static Date getSubDay(Date date, int dayInterval) {
        Date ret = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, dayInterval);
            ret = calendar.getTime();
        }
        return ret;
    }


    /**
     * @param dateStr
     * @param dayInterval
     * @return
     */
    public static String getHourSubTimeString(String dateStr, DateStyle dateStyle, DateStyle retDateStyle, int dayInterval) {
        String ret = null;
        try {

            Date otherDate = getSubHour(dateStr, dateStyle, dayInterval);
            ret = getDateTime(otherDate, retDateStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }





    /**
     * @param dateStr
     * @param hourInterval
     * @return
     */
    public static Date getSubHour(String dateStr, DateStyle dateStyle, int hourInterval) {
        Date date = StringToDate(dateStr, dateStyle);
        Date ret = getSubHour(date, hourInterval);
        return ret;
    }


    /**
     * @param date
     * @param hourInterval
     * @return
     */
    public static Date getSubHour(Date date, int hourInterval) {
        Date ret = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, hourInterval);
            ret = calendar.getTime();
        }

        return ret;
    }


    public static String string2String(String time, DateStyle dateStyle, DateStyle outPutDateStyle) {

        String ret = null;
        try {
            if (time != null) {
                ret = DateUtil.getDateTime(DateUtil.getDate(time, dateStyle), outPutDateStyle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


    public static Map<Long, String> getDaySubTimeStringMap(String gmtTime, DateStyle dateStyle, DateStyle retDateStyle, int interval) {

        Map<Long, String> dateMap = new HashedMap();
        try {
            for (int i = 0; i < interval + 1; i++) {
                String tmpTime = DateUtil.getDaySubTimeString(gmtTime, dateStyle, retDateStyle, i * -1);
                if (tmpTime != null) {
                    dateMap.put((long) i, tmpTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateMap;
    }

    public static Map<Long, String> getHourSubTimeStringMap(String gmtTime, DateStyle dateStyle, DateStyle retDateStyle, int interval) {

        Map<Long, String> dateMap = new HashedMap();
        try {
            for (int i = 0; i < interval + 1; i++) {
                String tmpTime = DateUtil.getHourSubTimeString(gmtTime, dateStyle, retDateStyle, i * -1);
                if (tmpTime != null) {
                    dateMap.put((long) i, tmpTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateMap;
    }


}