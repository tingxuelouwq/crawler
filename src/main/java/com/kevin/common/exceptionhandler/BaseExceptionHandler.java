package com.kevin.common.exceptionhandler;

import com.kevin.common.ret.RetJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

import static com.kevin.common.constant.BaseStatusCode.*;

/**
 * @类名：BaseExceptionHandler
 * @包名：com.kevin.exceptionhandler
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2017/7/7 15:29
 * @版本：1.0
 * @描述：异常处理器基础支撑类
 *            Exception                       HTTP Status Code
 * ConversionNotSupportedException         500 (Internal Server Error)
 * HttpMessageNotWritableException         500 (Internal Server Error)
 * HttpMediaTypeNotSupportedException      415 (Unsupported Media Type)
 * HttpMediaTypeNotAcceptableException     406 (Not Acceptable)
 * HttpRequestMethodNotSupportedException  405 (Method Not Allowed)
 * NoSuchRequestHandlingMethodException    404 (Not Found)
 * TypeMismatchException                   400 (Bad Request)
 * HttpMessageNotReadableException         400 (Bad Request)
 * MissingServletRequestParameterException 400 (Bad Request)
 */
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    /**
     * 处理400错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RetJson request400(HttpMessageNotReadableException ex) {
        log.error("ex", ex);
        return new RetJson(BAD_REQUEST, "请求参数解析失败");
    }

    /**
     * 处理400错误
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RetJson request400(MissingServletRequestParameterException ex) {
        log.error("ex", ex);
        return new RetJson(BAD_REQUEST, "缺少请求参数");
    }

    /**
     * 处理400错误
     */
    @ExceptionHandler(TypeMismatchException.class)
    public RetJson request400(TypeMismatchException ex) {
        log.error("ex", ex);
        return new RetJson(BAD_REQUEST, "参数类型不匹配");
    }

    /**
     * 处理405错误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public RetJson request405(HttpRequestMethodNotSupportedException ex) {
        log.error("ex", ex);
        return new RetJson(METHOD_NOT_SUPPORTED, "请求方式错误");
    }

    /**
     * 处理406错误
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public RetJson request406(HttpMediaTypeNotAcceptableException ex) {
        log.error("ex", ex);
        return new RetJson(HTTP_MEDIA_TYPE_NOT_ACCEPTABLE, "找不到可访问的多媒体类型");
    }

    /**
     * 处理500错误
     */
    @ExceptionHandler({ConversionNotSupportedException.class,
            HttpMessageNotWritableException.class})
    public RetJson request500(RuntimeException ex) {
        log.error("ex", ex);
        return new RetJson(INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public RetJson runtimeExceptionHandler(RuntimeException ex) {
        log.error("ex", ex);
        return new RetJson(RUNTIME_EXCEPTION, "服务器运行时异常");
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public RetJson nullPointExceptionHandler(NullPointerException ex) {
        log.error("ex", ex);
        return new RetJson(NULL_POINT_EXCEPTION, "空指针异常");
    }

    /**
     * 处理类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public RetJson classCastExceptionHandler(ClassCastException ex) {
        log.error("ex", ex);
        return new RetJson(CLASS_CAST_EXCEPTION, "类型转换异常");
    }

    /**
     * 处理IO异常
     */
    @ExceptionHandler(IOException.class)
    public RetJson ioExceptionHandler(IOException ex) {
        log.error("ex", ex);
        return new RetJson(IO_EXCEPTION, "IO异常");
    }

    /**
     * 处理未知方法异常
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public RetJson noSuchMethodExceptionHandler(NoSuchMethodException ex) {
        log.error("ex", ex);
        return new RetJson(NO_SUCH_METHOD_EXCEPTION, "未知方法异常");
    }

    /**
     * 处理数组越界异常
     */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public RetJson arrayIndexOutOfBoundsExceptionHandler(ArrayIndexOutOfBoundsException ex) {
        log.error("ex", ex);
        return new RetJson(ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION, "数组越界异常");
    }
}
