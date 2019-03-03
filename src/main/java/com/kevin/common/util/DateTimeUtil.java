package com.kevin.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 类名: DateTimeUtil<br/>
 * 包名：com.kevin.util<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2017/8/6 10:45<br/>
 * 版本：1.0<br/>
 * 描述：日期时间工具类<br/>
 */
public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER =
            DateTimeFormat.LONG_DATE_TIME_PATTERN_LINE.formatter;
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER =
            DateTimeFormat.SHORT_DATE_PATTERN_LINE.formatter;

    private DateTimeUtil() {
        // no constructor function
    }

    /**
     * GMT日期时间转中国日期时间
     * @param gmtDateTimeStr
     * @return
     */
    public static LocalDateTime parseGMTDateTimeToCN(String gmtDateTimeStr) {
        return parseDateTime(gmtDateTimeStr).plusHours(8);
    }

    /**
     * 字符串转日期时间，默认日期时间格式为：yyyy-MM-dd HH:mm:ss
     * @param dateTimeStr
     * @return
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_DATETIME_FORMATTER);
    }

    /**
     * 字符串转日期时间，使用指定的日期时间格式
     * @param dateTimeStr
     * @param format
     * @return
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, DateTimeFormat format) {
        return LocalDateTime.parse(dateTimeStr, format.formatter);
    }

    /**
     * 字符串转日期，默认日期格式为：yyyy-MM-dd
     * @param dateStr
     * @return
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr);
    }

    /**
     * 字符串转日期，使用指定的日期格式
     * @param dateStr
     * @param format
     * @return
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormat format) {
        return LocalDate.parse(dateStr, format.formatter);
    }

    /**
     * 日期时间转字符串，默认日期时间格式为：yyyy-MM-dd HH:mm:ss
     * @param dateTime
     * @return
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return DEFAULT_DATETIME_FORMATTER.format(dateTime);
    }

    /**
     * 日期时间转字符串，使用指定的日期时间格式
     * @param dateTime
     * @param format
     * @return java.lang.String
     */
    public static String formatDateTime(LocalDateTime dateTime, DateTimeFormat format) {
        return format.formatter.format(dateTime);
    }

    /**
     * 日期转字符串，默认日期格式为：yyyy-MM-dd
     * @param date
     * @return
     */
    public static String formatDate(LocalDate date) {
        return DEFAULT_DATE_FORMATTER.format(date);
    }

    /**
     * 日期转字符串，使用指定的日期格式
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(LocalDate date, DateTimeFormat format) {
        return format.formatter.format(date);
    }

    /**
     * 毫秒数转日期时间
     * @param epochMilli
     * @return
     */
    public static LocalDateTime parseEpochMilli(long epochMilli) {
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 日期时间转毫秒
     * @param dateTime
     * @return
     */
    public static long toEpochMilli(LocalDateTime dateTime) {
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 获取当前日期时间，默认日期时间格式为：yyyy-MM-dd HH:mm:ss
     * @param
     * @return java.lang.String
     */
    public static String getCurrentDateTimeStr() {
        return DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
    }

    /**
     * 获取当前日期时间，使用指定的日期时间格式
     * @param format
     * @return java.lang.String
     */
    public static String getCurrentDateTimeStr(DateTimeFormat format) {
        return format.formatter.format(LocalDateTime.now());
    }

    /**
     * 获取当前日期，默认日期格式为：yyyy-MM-dd
     * @return
     */
    public static String getCurrentDateStr() {
        return DEFAULT_DATE_FORMATTER.format(LocalDate.now());
    }

    /**
     * 获取当前日期，使用指定的日期时间格式
     * @param format
     * @return
     */
    public static String getCurrentDateStr(DateTimeFormat format) {
        return format.formatter.format(LocalDate.now());
    }

    /**
     * 获取当前日期时间
     * @return
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * 获取当期日期
     * @return
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * 日期转换成当天起始日期时间
     * @param date
     * @return
     */
    public static LocalDateTime atStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 日期转换成当天结束日期时间
     * @param date
     * @return
     */
    public static LocalDateTime atEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /**
     * 日期时间格式
     */
    public enum DateTimeFormat {

        /** 短时间格式 **/
        SHORT_DATE_PATTERN_LINE("yyyy-MM-dd"),
        SHORT_DATE_PATTERN_SLASH("yyyy/MM/dd"),
        SHORT_DATE_PATTERN_DOUBLE_SLASH("yyyy\\MM\\dd"),
        SHORT_DATE_PATTERN_NONE("yyyyMMdd"),

        /** 长时间格式 **/
        LONG_DATE_TIME_PATTERN_LINE("yyyy-MM-dd HH:mm:ss"),
        LONG_DATE_TIME_PATTERN_SLASH("yyyy/MM/dd HH:mm:ss"),
        LONG_DATE_TIME_PATTERN_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss"),
        LONG_DATE_TIME_PATTERN_NONE("yyyyMMdd HH:mm:ss"),
        LONG_DATE_TIME_PATTERN_NONE_SEPARATOR("yyyyMMddHHmmss"),
        LONG_DATE_TIME_PATTERN_WITH_MILSEC_LINE("yyyy-MM-dd HH:mm:ss.SSS"),
        LONG_DATE_TIME_PATTERN_WITH_MILSEC_SLASH("yyyy/MM/dd HH:mm:ss.SSS"),
        LONG_DATE_TIME_PATTERN_WITH_MILSEC_DOUBLE_SLASH("yyyy\\MM\\dd HH:mm:ss.SSS"),
        LONG_DATE_TIME_PATTERN_WITH_MILSEC_NONE("yyyyMMdd HH:mm:ss.SSS"),
        LONG_DATE_TIME_PATTERN_WITH_MILSEC_NONE_SEPARATOR("yyyyMMddHHmmssSSS"),
        LONG_DATE_TIME_PATTERN_WITH_LINE_TO_HOUR("yyyy-MM-dd-HH");

        private DateTimeFormatter formatter;

        DateTimeFormat(String pattern) {
            formatter = DateTimeFormatter.ofPattern(pattern);
        }
    }
}