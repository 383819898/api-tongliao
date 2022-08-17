package com.chinagpay.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期操作辅助类
 *
 * @author dong.gang
 * @version $Id: DateUtil.java, v 0.1 2014年3月28日 上午8:58:11 dong.gang Exp $
 */
public final class DateUtil {
    /**
     * 锁对象
     */
    private static final Object LOCK_OBJ = new Object();
    private static final Map<String, ThreadLocal<DateFormat>> FORMAT_MAP = new HashMap<>();

    private DateUtil() {}

    /**
     * 日期格式
     **/
    public interface DATE_PATTERN {
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String YYYYMMDD = "yyyyMMdd";
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * @param pattern
     *            pattern
     * @return DateFormat
     */
    public static DateFormat getDateFormat(final String pattern) {
        ThreadLocal<DateFormat> format = FORMAT_MAP.get(pattern);
        if (format == null) {
            synchronized (LOCK_OBJ) {
                format = FORMAT_MAP.get(pattern);
                if (format == null) {
                    format = new ThreadLocal<DateFormat>() {
                        @Override
                        protected DateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    FORMAT_MAP.put(pattern, format);
                }
            }
        }
        return format.get();
    }

    /**
     * 获取当前日期
     *
     * @param pattern
     * @return
     */
    public static String getCurrentDT(String pattern) {
        return format(Calendar.getInstance().getTime(), pattern);
    }

    /**
     * 获取默认格式当前时间（yyyyMMddHHmmss）
     *
     * @return
     */
    public static String getCurrentTime() {
        return getCurrentDT(DATE_PATTERN.YYYYMMDDHHMMSS);
    }

    /**
     * @param nextDays
     *            next days
     * @param pattern
     *            format pattern
     * @return date format
     */
    public static String getNextDT(int nextDays, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, nextDays);
        return format(cal.getTime(), pattern);
    }

    /**
     * @param baseDT
     *            base date
     * @param nextDays
     *            next days
     * @param pattern
     *            format pattern
     * @return date format if parse date error
     */
    public static String getNextDT(String baseDT, int nextDays, String pattern) throws ParseException {
        return getNextDT(baseDT, Calendar.DAY_OF_YEAR, nextDays, pattern);
    }

    /**
     * 以某个时间为参照获取指定间隔的另外一个时间
     *
     * @param baseDT
     * @param field
     *            see Calendar fields
     * @param amount
     *            the amount of date or time to be added to the field.
     * @param pattern
     * @return
     */
    public static String getNextDT(String baseDT, int field, int amount, String pattern) throws ParseException {
        DateFormat format = getDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(format.parse(baseDT));
        cal.add(field, amount);
        return format.format(cal.getTime());
    }

    /**
     * 以某个时间为参照获取指定间隔的另外一个时间
     *
     * @param baseDT
     * @param field
     *            see Calendar fields
     * @param amount
     *            the amount of date or time to be added to the field.
     * @param pattern
     * @return
     */
    public static String getNextDT(Date baseDT, int field, int amount, String pattern) {
        DateFormat format = getDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDT);
        cal.add(field, amount);
        return format.format(cal.getTime());
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param date
     * @return
     */
    public static final String format(Object date) {
        return format(date, DATE_PATTERN.YYYY_MM_DD);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static final String format(Object date, String pattern) {
        if (date == null) {
            return null;
        }
        if (pattern == null) {
            return format(date);
        }
        return getDateFormat(pattern).format(date);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @return
     */
    public static final String format(String date, String srcPattern, String outPattern) {
        if (date == null) {
            return null;
        }
        try {
            Date d = getDateFormat(srcPattern).parse(date);
            if (outPattern == null) {
                return format(d);
            }
            return getDateFormat(outPattern).format(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取日期 pattern:YYYYMMDD
     *
     * @return
     */
    public static final String getDate() {
        return format(new Date(), DATE_PATTERN.YYYYMMDD);
    }

    /**
     * 获取日期时间
     *
     * @return
     */
    public static final String getDateTime() {
        return format(new Date(), DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取日期
     *
     * @param pattern
     * @return
     */
    public static final String getDateTime(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 日期计算
     *
     * @param date
     * @param field
     * @param amount
     * @return
     */
    public static final Date addDate(Date date, int field, int amount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 字符串转换为日期:不支持yyM[M]d[d]格式
     *
     * @param date
     * @return
     */
    public static final Date stringToDate(String date) {
        if (date == null) {
            return null;
        }
        String separator = String.valueOf(date.charAt(4));
        String pattern = "yyyyMMdd";
        if (!separator.matches("\\d*")) {
            pattern = "yyyy" + separator + "MM" + separator + "dd";
            if (date.length() < 10) {
                pattern = "yyyy" + separator + "M" + separator + "d";
            }
        } else if (date.length() < 8) {
            pattern = "yyyyMd";
        }
        pattern += " HH:mm:ss.SSS";
        pattern = pattern.substring(0, Math.min(pattern.length(), date.length()));
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 间隔天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getDayBetween(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        long n = end.getTimeInMillis() - start.getTimeInMillis();
        return (int)(n / (60 * 60 * 24 * 1000l));
    }

    /**
     * 间隔月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        return n;
    }

    /**
     * 间隔月，多一天就多算一个月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static final Integer getMonthBetweenWithDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            return null;
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        int year1 = start.get(Calendar.YEAR);
        int year2 = end.get(Calendar.YEAR);
        int month1 = start.get(Calendar.MONTH);
        int month2 = end.get(Calendar.MONTH);
        int n = (year2 - year1) * 12;
        n = n + month2 - month1;
        int day1 = start.get(Calendar.DAY_OF_MONTH);
        int day2 = end.get(Calendar.DAY_OF_MONTH);
        if (day1 <= day2) {
            n++;
        }
        return n;
    }

    /**
     * 封装simpleDateFormat 的 parse 方法
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getDateFormat(pattern).parse(dateStr);
    }
}
