package com.kevin.proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: ProxyDto<br/>
 * 包名：com.kevin.proxy.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 20:04<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Getter
@Setter
@AllArgsConstructor
public class ProxyDto implements Serializable {

    private static final long serialVersionUID = 8279655421421110729L;
    private String host;
    private String port;
    private String type;
}
