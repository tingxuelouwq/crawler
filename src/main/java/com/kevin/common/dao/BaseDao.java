package com.kevin.common.dao;

import com.kevin.common.entity.BaseEntity;

/**
 * @类名: BaseDao
 * @包名：com.kevin.dao
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2017/7/18 14:22
 * @版本：1.0
 * @描述：数据访问层基础支撑接口
 */
public interface BaseDao<T extends BaseEntity> {

    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    T selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
