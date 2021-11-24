package com.github.cbyzzy.service;

import com.github.pagehelper.PageHelper;
import com.github.cbyzzy.criteria.HTCriteria;
import com.github.cbyzzy.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 抽象基类service
 *
 * @param <T>
 */
public abstract class BaseService<T> {

    @Autowired
    protected Mapper<T> mapper;

    /**
     * 新增
     *
     * @param record
     * @return
     */
    public T save(T record) {
        mapper.insertSelective(record);
        return record;
    }

    /**
     * 修改
     *
     * @param record
     * @return
     */
    public Integer update(T record) {
        return mapper.updateByPrimaryKeySelective(record);
    }


    /**
     * 删除
     *
     * @param key
     * @return
     */
    public Integer delete(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }


    /**
     * 主键查询
     *
     * @param key
     * @return
     */
    public T findOne(Object key) {
        return mapper.selectByPrimaryKey(key);
    }

    /**
     * 条件查询
     *
     * @param criteria
     * @return
     */
    public List<T> find(HTCriteria<T> criteria) {
        return mapper.selectByExample(criteria.example());
    }
    /**
     * 条件查询
     *
     * @param example
     * @return
     */
    public List<T> find(Example example) {
        return mapper.selectByExample(example);
    }
    /**
     * 查询所有数据
     *
     * @return
     */
    public List<T> find() {
        return mapper.selectAll();
    }

    public int count(T t) {
        return mapper.selectCount(t);
    }


    /**
     * 分页查询符合条件
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<T> findPage(HTCriteria<T> criteria, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = find(criteria);
        return new PageResult<>(list);
    }

    /**
     * 分页查询
     * @param example
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<T> findPage(Example example, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = mapper.selectByExample(example);
        return new PageResult<>(list);
    }

    /**
     * 分页查询所有数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult<T> findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = find();
        return new PageResult<>(list);
    }

}
