package com.kevin.common.constant;

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
    public static final int BAD_REQUEST = 400;
    public static final int FORBIDDEN = 403;
    public static final int METHOD_NOT_SUPPORTED = 405;
    public static final int HTTP_MEDIA_TYPE_NOT_ACCEPTABLE = 406;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;

    // 程序异常码
    public static final int RUNTIME_EXCEPTION = 100100;
    public static final int NULL_POINT_EXCEPTION = 100101;
    public static final int CLASS_CAST_EXCEPTION = 100102;
    public static final int IO_EXCEPTION = 100103;
    public static final int NO_SUCH_METHOD_EXCEPTION = 100104;
    public static final int ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION = 100105;
    public static final int HTTP_URL_CONNECTION_EXCEPTION = 100106;
}
