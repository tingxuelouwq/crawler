package com.kevin.cookie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: TCWDto<br/>
 * 包名：com.kevin.cookie.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 23:11<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Getter
@Setter
@AllArgsConstructor
public class TCWDto implements Serializable {
    private static final long serialVersionUID = 8714547104174255516L;

    private String tid;
    private String confidence;
    private String where;
}
