package com.kevin.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：StringUtil<br/>
 * 包名：com.kevin.util<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2017/7/7 10:33<br/>
 * 版本：1.0<br/>
 * 描述：字符串操工具类<br/>
 */
public class StringUtil {

    private StringUtil() {
        // no constructor function
    }

    /**
     * 判断字符串是否为空，空字符串或者null时返回true
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否非空，非空字符串并且非null时返回true
     * @param str
     * @return boolean
     */
    public static boolean isNotEmpty(String str) {
       return str != null && str.length() != 0;
    }

    /**
     * 将数组转换成字符串，以逗号分隔，跳过空字符串
     * @param strArr
     * @return java.lang.String
     */
    public static String convert(String... strArr) {
        return convert(strArr, ",");
    }

    /**
     * 将数组转换成字符串，以指定分隔符分隔，跳过空字符串
     * @param strArr
     * @param separator
     * @return String
     */
    public static String convert(String[] strArr, String separator) {
        if (strArr.length == 0) {
            return "";
        }

        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if (isEmpty(strArr[i])) {
                continue;
            }

            if (i > 0) {
                strBuilder.append(separator).append(strArr[i]);
            } else {
                strBuilder.append(strArr[i]);
            }
        }

        return strBuilder.toString();
    }

    /**
     * 根据指定分隔符拆分字符串
     * @param str 要拆分的字符串，可以为null
     * @param separatorChar 分隔符
     * @return java.lang.String[] 拆分得到的字符串数组，如果输入为null的话，则返回null
     */
    public static String[] split(final String str, final char separatorChar) {
        return split(str, separatorChar, false);
    }

    /**
     * 根据指定分隔符拆分字符串
     * <pre>
     * StringUtils.split(null) = null
     * StringUtils.split(",", ',', false) = []
     * StringUtils.split(",", ',', true) = ["",""]
     * </pre>
     * @param str 要拆分的字符串，可以为null
     * @param separatorChar 分隔符
     * @param preserveAllTokens 如果设置为false，则相邻分隔符会被视为一个分隔符
     * @return java.lang.String[] 拆分得到的字符串数组，如果输入为null的话，则返回null
     */
    public static String[] split(final String str, final char separatorChar, final boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }
        final List<String> list = new ArrayList<>();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match || preserveAllTokens) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }
}