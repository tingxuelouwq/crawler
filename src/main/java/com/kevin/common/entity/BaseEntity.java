package com.kevin.common.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @类名: BaseEntity
 * @包名：com.kevin.entity
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2017/7/18 14:04
 * @版本：1.0
 * @描述：实体类基类
 */
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 2461597661350564526L;

    /**
     * 主键id
     **/
    private Long id;

    /**
     * 版本号
     */
    private Integer version = 1;

    /**
     * 创建时间
     **/
    private Date createTime = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
