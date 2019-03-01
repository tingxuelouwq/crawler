package com.kevin.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: ActInfoDto<br/>
 * 包名：com.kevin.crawler.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/26 11:07<br/>
 * 版本：1.0<br/>
 * 描述：转发、评论、点赞Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class ActInfoDto implements Serializable {

    private static final long serialVersionUID = 8209027718377276021L;
    /** 转发数 **/
    private String forwardNum;
    /** 评论数 **/
    private String commentNum;
    /** 点赞数 **/
    private String likeNum;
}
