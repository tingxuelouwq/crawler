package com.kevin.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 类名: BlogPublishInfoDto<br/>
 * 包名：com.kevin.crawler.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/26 10:30<br/>
 * 版本：1.0<br/>
 * 描述：微博发布信息Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class BlogPublishInfoDto implements Serializable {

    private static final long serialVersionUID = 5089795998738349667L;
    /** 博文独立网址 **/
    private String blogLink;
    /** 发布时间 **/
    private LocalDateTime publishDateTime;
}
