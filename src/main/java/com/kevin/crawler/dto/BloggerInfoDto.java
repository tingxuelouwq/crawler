package com.kevin.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 类名: BloggerInfoDto<br/>
 * 包名：com.kevin.crawler.dto<br/>
 * 作者：kevin<br/>
 * 时间：2019/2/25 20:35<br/>
 * 版本：1.0<br/>
 * 描述：博主信息Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class BloggerInfoDto implements Serializable {

    private static final long serialVersionUID = 7399489611501670636L;
    /** 博主id **/
    private String id;
    /** 博主昵称 **/
    private String nickName;
    /** 博主主页 **/
    private String homePage;
    /** 博主认证信息 **/
    private String authInfo;
}
