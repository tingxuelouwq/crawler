package com.kevin.crawler.exceptionhandler;

import com.kevin.common.ret.RetJson;
import com.kevin.crawler.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @类名: BizExceptionHandler
 * @包名：org.xinhua.statistics.exceptionhandler
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/2/8 16:19
 * @版本：1.0
 * @描述：
 */
@RestControllerAdvice
@Slf4j
public class BizExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    public RetJson bizExceptionHandler(BizException ex) {
        log.error("业务异常: ", ex);
        return new RetJson(ex.getCode(), ex.getMessage());
    }
}
