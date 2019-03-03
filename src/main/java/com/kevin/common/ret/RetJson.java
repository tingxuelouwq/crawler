package com.kevin.common.ret;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @类名: RetJson
 * @包名：org.xinhua.common.ret
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/1/30 14:46
 * @版本：1.0
 * @描述：Json格式响应
 */
@Getter
@Setter
@AllArgsConstructor
public class RetJson implements Serializable {

    private static final long serialVersionUID = 1155124090420409354L;

    /**
     * 状态码
     **/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int code;

    /**
     * 提示信息
     **/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    /**
     * 业务数据
     **/
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public RetJson(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
