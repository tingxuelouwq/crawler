package com.kevin.common.controller;

import com.kevin.common.ret.RetJson;

/**
 * @类名：BaseController
 * @包名：com.kevin.controller
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2017/7/7 14:32
 * @版本：1.0
 * @描述：控制层基类，所有控制层类均继承于此类
 */
public abstract class BaseController {
    /**
     * 返回Json格式响应
     */
    protected RetJson retResponse(int code, String message) {
        return new RetJson(code, message);
    }

    /**
     * 返回Json格式响应
     */
    protected RetJson retResponse(int code, String message, Object data) {
        return new RetJson(code, message, data);
    }
}
