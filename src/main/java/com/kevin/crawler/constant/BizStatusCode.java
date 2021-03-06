package com.kevin.crawler.constant;

import com.kevin.common.constant.BaseStatusCode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名: BizStatusCode<br/>
 * 包名：com.kevin.crawler.constant<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/28 10:28<br/>
 * 版本：1.0<br/>
 * 描述：业务状态码
 */
public class BizStatusCode extends BaseStatusCode {

    // 成功
    public static final int SUCCESS = 200;
    // 日期格式有误
    public static final int DATE_FORMAT_ERROR = 200201;
    // 读取微博数据错误
    public static final int READ_OR_CONNECT_ERROR = 200202;
    // 爬虫任务失败
    public static final int CRAWL_TASK_FAILUER = 200203;
    // 存盘失败
    public static final int SAVE_TO_DISK_FAILED = 200204;
    // 流关闭错误
    public static final int OUTPUTSTREAM_CLOSED_ERROR = 200205;

    // 状态码-提示信息映射表
    public static final Map<Integer, String> bizStatusCodeMap;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(SUCCESS, "成功");
        map.put(DATE_FORMAT_ERROR, "日期格式有误，正确格式样例: 2019-01-01-0");
        map.put(READ_OR_CONNECT_ERROR, "与微博链接失败或读取微博数据超时，请检查网络连接");
        map.put(CRAWL_TASK_FAILUER, "爬虫任务失败，可能是被微博封杀，请稍后再试");
        map.put(SAVE_TO_DISK_FAILED, "存盘失败，请检查是否已经打开该文件");
        map.put(OUTPUTSTREAM_CLOSED_ERROR, "流关闭错误");
        bizStatusCodeMap = Collections.unmodifiableMap(map);
    }
}
