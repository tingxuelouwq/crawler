package com.kevin.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: BlogDto<br/>
 * 包名：com.kevin.crawler.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/28 14:36<br/>
 * 版本：1.0<br/>
 * 描述：博文Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class BlogDto implements Serializable {

    private BloggerInfoDto bloggerInfo;
    private BlogTextDto blogText;
    private BlogPublishInfoDto blogPublishInfo;
    private ActInfoDto actInfo;
    private ForwardedBlogDto forwardedBlog;
}
