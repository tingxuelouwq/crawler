package com.kevin.common.service.impl;

import com.kevin.common.dao.BaseDao;
import com.kevin.common.entity.BaseEntity;
import com.kevin.common.service.BaseService;

/**
 * @类名: BaseServiceImpl
 * @包名：com.kevin.service.impl
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2017/7/18 14:30
 * @版本：1.0
 * @描述：业务层基础支撑类
 */
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    private BaseDao<T> baseDao;

    public void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return baseDao.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(T record) {
        return baseDao.insert(record);
    }

    @Override
    public int insertSelective(T record) {
        return baseDao.insertSelective(record);
    }

    @Override
    public T selectByPrimaryKey(Long id) {
        return baseDao.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(T record){
        return baseDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(T record) {
        return baseDao.updateByPrimaryKeySelective(record);
    }
}
