package com.kevin.common.service;

import com.kevin.common.entity.BaseEntity;

/**
 * @接口名: BaseService
 * @包名：com.kevin.service
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2017/7/18 14:22
 * @版本：1.0
 * @描述：业务层基础支撑接口
 */
public interface BaseService<T extends BaseEntity> {

    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
