package com.kevin.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: ForwardedBlogDto<br/>
 * 包名：com.kevin.crawler.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/28 14:43<br/>
 * 版本：1.0<br/>
 * 描述：被转发的微博Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class ForwardedBlogDto implements Serializable {

    private BloggerInfoDto bloggerInfo;
    private BlogTextDto blogText;
    private BlogPublishInfoDto blogPublishInfo;
    private ActInfoDto actInfo;
}
