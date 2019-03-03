package com.kevin.crawler.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @类名: BizException
 * @包名：org.xinhua.statistics.exception
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/2/8 14:58
 * @版本：1.0
 * @描述：业务异常类
 */
@Getter
@AllArgsConstructor
public class BizException extends Exception {
    private static final long serialVersionUID = 1355793179034183064L;

    private int code;
    private String message;

    public BizException(Throwable cause, int code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }
}
