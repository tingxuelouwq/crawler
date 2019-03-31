package com.kevin.common.exceptionhandler;

import com.kevin.common.ret.RetJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
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
 * Exception                               HTTP Status Code
 * ConversionNotSupportedException         500 (Internal Server Error)
 * HttpMessageNotWritableException         500 (Internal Server Error)
 * HttpMediaTypeNotSupportedException      415 (Unsupported Media Type)
 * HttpMediaTypeNotAcceptableException     406 (Not Acceptable)
 * HttpRequestMethodNotSupportedException  405 (Method Not Allowed)
 * TypeMismatchException                   400 (Bad Request)
 * MissingServletRequestParameterException 400 (Bad Request)
 * HttpMessageNotReadableException         400 (Bad Request)
 */
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RetJson request400(HttpMessageNotReadableException ex) {
        log.error("ex", ex);
        return new RetJson(PARAM_RESOLVE_FAILED, baseStatusCodeMap.get(PARAM_RESOLVE_FAILED));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RetJson request400(MissingServletRequestParameterException ex) {
        log.error("ex", ex);
        return new RetJson(LACK_OF_PARAM, baseStatusCodeMap.get(LACK_OF_PARAM));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public RetJson request400(TypeMismatchException ex) {
        log.error("ex", ex);
        return new RetJson(PARAM_TYPE_NOT_MATCH, baseStatusCodeMap.get(PARAM_TYPE_NOT_MATCH));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public RetJson request405(HttpRequestMethodNotSupportedException ex) {
        log.error("ex", ex);
        return new RetJson(METHOD_NOT_SUPPORTED, baseStatusCodeMap.get(METHOD_NOT_SUPPORTED));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public RetJson request406(HttpMediaTypeNotAcceptableException ex) {
        log.error("ex", ex);
        return new RetJson(HTTP_MEDIA_TYPE_NOT_ACCEPTABLE, baseStatusCodeMap.get(HTTP_MEDIA_TYPE_NOT_ACCEPTABLE));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public RetJson request406(HttpMediaTypeNotSupportedException ex) {
        log.error("ex", ex);
        return new RetJson(HTTP_MEDIA_TYPE_NOT_SUPPORT, baseStatusCodeMap.get(HTTP_MEDIA_TYPE_NOT_SUPPORT));
    }

    @ExceptionHandler({ConversionNotSupportedException.class,
            HttpMessageNotWritableException.class})
    public RetJson request500(RuntimeException ex) {
        log.error("ex", ex);
        return new RetJson(INTERNAL_SERVER_ERROR, baseStatusCodeMap.get(INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(RuntimeException.class)
    public RetJson runtimeExceptionHandler(RuntimeException ex) {
        log.error("ex", ex);
        return new RetJson(RUNTIME_EXCEPTION, baseStatusCodeMap.get(RUNTIME_EXCEPTION));
    }

    @ExceptionHandler(NullPointerException.class)
    public RetJson nullPointExceptionHandler(NullPointerException ex) {
        log.error("ex", ex);
        return new RetJson(NULL_POINT_EXCEPTION, baseStatusCodeMap.get(NULL_POINT_EXCEPTION));
    }

    /**
     * 处理类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public RetJson classCastExceptionHandler(ClassCastException ex) {
        log.error("ex", ex);
        return new RetJson(CLASS_CAST_EXCEPTION, baseStatusCodeMap.get(CLASS_CAST_EXCEPTION));
    }

    /**
     * 处理IO异常
     */
    @ExceptionHandler(IOException.class)
    public RetJson ioExceptionHandler(IOException ex) {
        log.error("ex", ex);
        return new RetJson(IO_EXCEPTION, baseStatusCodeMap.get(IO_EXCEPTION));
    }

    /**
     * 处理未知方法异常
     */
    @ExceptionHandler(NoSuchMethodException.class)
    public RetJson noSuchMethodExceptionHandler(NoSuchMethodException ex) {
        log.error("ex", ex);
        return new RetJson(NO_SUCH_METHOD_EXCEPTION, baseStatusCodeMap.get(NO_SUCH_METHOD_EXCEPTION));
    }

    /**
     * 处理数组越界异常
     */
    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public RetJson arrayIndexOutOfBoundsExceptionHandler(ArrayIndexOutOfBoundsException ex) {
        log.error("ex", ex);
        return new RetJson(ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION, baseStatusCodeMap.get(ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION));
    }
}
