package com.kevin.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: BlogTextDto<br/>
 * 包名：com.kevin.crawler.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/25 20:48<br/>
 * 版本：1.0<br/>
 * 描述：微博正文Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class BlogTextDto implements Serializable {

    /** 正文(带表情说明) **/
    private String imgText;
}
