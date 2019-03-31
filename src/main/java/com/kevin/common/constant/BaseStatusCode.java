package com.kevin.common.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @interface: BaseStatusCode
 * @package: org.xinhua.common.constant
 * @author: kevin[wangqi2017@xinhua.org]
 * @date: 2018/1/30 14:10
 * @version: 1.0
 * @desc: 状态码
 */
public class BaseStatusCode {

    // 响应异常码
    public static final int PARAM_RESOLVE_FAILED = 400;
    public static final int LACK_OF_PARAM = 400;
    public static final int PARAM_TYPE_NOT_MATCH = 400;
    public static final int METHOD_NOT_SUPPORTED = 405;
    public static final int HTTP_MEDIA_TYPE_NOT_ACCEPTABLE = 406;
    public static final int HTTP_MEDIA_TYPE_NOT_SUPPORT = 415;
    public static final int INTERNAL_SERVER_ERROR = 500;

    // 程序异常码
    public static final int RUNTIME_EXCEPTION = 100100;
    public static final int NULL_POINT_EXCEPTION = 100101;
    public static final int CLASS_CAST_EXCEPTION = 100102;
    public static final int IO_EXCEPTION = 100103;
    public static final int NO_SUCH_METHOD_EXCEPTION = 100104;
    public static final int ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION = 100105;
    public static final int HTTP_URL_CONNECTION_EXCEPTION = 100106;

    // 状态码-提示信息映射表
    public static final Map<Integer, String> baseStatusCodeMap;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(PARAM_RESOLVE_FAILED, "请求参数解析失败, 请检查请求参数是否正确");
        map.put(LACK_OF_PARAM, "缺少请求参数, 请检查请求参数是否正确");
        map.put(PARAM_TYPE_NOT_MATCH, "参数类型不匹配, 请检查请求参数是否正确");
        map.put(METHOD_NOT_SUPPORTED, "请求方式错误, 请检查请求方式是否正确");
        map.put(HTTP_MEDIA_TYPE_NOT_ACCEPTABLE, "找不到可访问的多媒体类型, 请检查Content-Type是否正确");
        map.put(HTTP_MEDIA_TYPE_NOT_SUPPORT, "不支持的多媒体类型, 请检查Content-Type是否正确");
        map.put(INTERNAL_SERVER_ERROR, "服务器内部错误");
        map.put(RUNTIME_EXCEPTION, "服务器运行时异常");
        map.put(NULL_POINT_EXCEPTION, "空指针异常");
        map.put(CLASS_CAST_EXCEPTION, "类型转换异常");
        map.put(IO_EXCEPTION, "IO异常");
        map.put(NO_SUCH_METHOD_EXCEPTION, "未知方法异常");
        map.put(ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION, "数组越界异常");
        baseStatusCodeMap = Collections.unmodifiableMap(map);
    }
}
