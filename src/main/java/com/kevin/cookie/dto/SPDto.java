package com.kevin.cookie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: SPDto<br/>
 * 包名：com.kevin.cookie.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/3/31 23:57<br/>
 * 版本：1.0<br/>
 * 描述：
 */
@Getter
@Setter
@AllArgsConstructor
public class SPDto implements Serializable {

    private static final long serialVersionUID = 6207238702655161559L;
    private String sub;
    private String subp;
}
