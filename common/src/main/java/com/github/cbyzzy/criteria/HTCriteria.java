package com.github.cbyzzy.criteria;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;
import tk.mybatis.mapper.weekend.Fn;
import tk.mybatis.mapper.weekend.reflection.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 查询条件封装
 * Author:cbyzzy
 */
public class HTCriteria<T> implements tk.mybatis.mapper.entity.SqlsCriteria {

    private Sqls.Criteria criteria;

    private Class<T> clazz;

    private List<Map<String, String>> orderList = new ArrayList<>();

    private HTCriteria(Class<T> clazz) {
        this.clazz = clazz;
        this.criteria = new Sqls.Criteria();
    }

    public static <T> HTCriteria<T> getInstance(Class<T> clazz) {
        return new HTCriteria<>(clazz);
    }

    public HTCriteria<T> andIsNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is null", "and"));
        return this;
    }

    public HTCriteria<T> andIsNull(Fn<T, Object> fn) {
        return this.andIsNull(Reflections.fnToFieldName(fn));
    }

    public HTCriteria<T> andIsNotNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is not null", "and"));
        return this;
    }

    public HTCriteria<T> andIsNotNull(Fn<T, Object> fn) {
        return this.andIsNotNull(Reflections.fnToFieldName(fn));
    }

    public HTCriteria<T> andEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "=", "and"));
        return this;
    }

    public HTCriteria<T> andEqualTo(Fn<T, Object> fn, Object value) {
        return this.andEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andNotEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<>", "and"));
        return this;
    }

    public HTCriteria<T> andNotEqualTo(Fn<T, Object> fn, Object value) {
        return this.andNotEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andGreaterThan(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">", "and"));
        return this;
    }

    public HTCriteria<T> andGreaterThan(Fn<T, Object> fn, Object value) {
        return this.andGreaterThan(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andGreaterThanOrEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">=", "and"));
        return this;
    }

    public HTCriteria<T> andGreaterThanOrEqualTo(Fn<T, Object> fn, Object value) {
        return this.andGreaterThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andLessThan(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<", "and"));
        return this;
    }

    public HTCriteria<T> andLessThan(Fn<T, Object> fn, Object value) {
        return this.andLessThan(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andLessThanOrEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<=", "and"));
        return this;
    }

    public HTCriteria<T> andLessThanOrEqualTo(Fn<T, Object> fn, Object value) {
        return this.andLessThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andIn(String property, Iterable values) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "in", "and"));
        return this;
    }

    public HTCriteria<T> andIn(Fn<T, Object> fn, Iterable values) {
        return this.andIn(Reflections.fnToFieldName(fn), values);
    }

    public HTCriteria<T> andNotIn(String property, Iterable values) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "not in", "and"));
        return this;
    }

    public HTCriteria<T> andNotIn(Fn<T, Object> fn, Iterable values) {
        return this.andNotIn(Reflections.fnToFieldName(fn), values);
    }

    public HTCriteria<T> andBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "between", "and"));
        return this;
    }

    public HTCriteria<T> andBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.andBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public HTCriteria<T> andNotBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "not between", "and"));
        return this;
    }

    public HTCriteria<T> andNotBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.andNotBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public HTCriteria<T> andLike(String property, String value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "like", "and"));
        return this;
    }

    public HTCriteria<T> andLike(Fn<T, Object> fn, String value) {
        return this.andLike(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> andNotLike(String property, String value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "not like", "and"));
        return this;
    }

    public HTCriteria<T> andNotLike(Fn<T, Object> fn, String value) {
        return this.andNotLike(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orIsNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is null", "or"));
        return this;
    }

    public HTCriteria<T> orIsNull(Fn<T, Object> fn) {
        return this.orIsNull(Reflections.fnToFieldName(fn));
    }

    public HTCriteria<T> orIsNotNull(String property) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, "is not null", "or"));
        return this;
    }

    public HTCriteria<T> orIsNotNull(Fn<T, Object> fn) {
        return this.orIsNotNull(Reflections.fnToFieldName(fn));
    }

    public HTCriteria<T> orEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "=", "or"));
        return this;
    }

    public HTCriteria<T> orEqualTo(Fn<T, Object> fn, String value) {
        return this.orEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orNotEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<>", "or"));
        return this;
    }

    public HTCriteria<T> orNotEqualTo(Fn<T, Object> fn, String value) {
        return this.orNotEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orGreaterThan(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">", "or"));
        return this;
    }

    public HTCriteria<T> orGreaterThan(Fn<T, Object> fn, String value) {
        return this.orGreaterThan(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orGreaterThanOrEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, ">=", "or"));
        return this;
    }

    public HTCriteria<T> orGreaterThanOrEqualTo(Fn<T, Object> fn, String value) {
        return this.orGreaterThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orLessThan(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<", "or"));
        return this;
    }

    public HTCriteria<T> orLessThan(Fn<T, Object> fn, String value) {
        return this.orLessThan(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orLessThanOrEqualTo(String property, Object value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "<=", "or"));
        return this;
    }

    public HTCriteria<T> orLessThanOrEqualTo(Fn<T, Object> fn, String value) {
        return this.orLessThanOrEqualTo(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orIn(String property, Iterable values) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "in", "or"));
        return this;
    }

    public HTCriteria<T> orIn(Fn<T, Object> fn, Iterable values) {
        return this.orIn(Reflections.fnToFieldName(fn), values);
    }

    public HTCriteria<T> orNotIn(String property, Iterable values) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, values, "not in", "or"));
        return this;
    }

    public HTCriteria<T> orNotIn(Fn<T, Object> fn, Iterable values) {
        return this.orNotIn(Reflections.fnToFieldName(fn), values);
    }

    public HTCriteria<T> orBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "between", "or"));
        return this;
    }

    public HTCriteria<T> orBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.orBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public HTCriteria<T> orNotBetween(String property, Object value1, Object value2) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value1, value2, "not between", "or"));
        return this;
    }

    public HTCriteria<T> orNotBetween(Fn<T, Object> fn, Object value1, Object value2) {
        return this.orNotBetween(Reflections.fnToFieldName(fn), value1, value2);
    }

    public HTCriteria<T> orLike(String property, String value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "like", "or"));
        return this;
    }

    public HTCriteria<T> orLike(Fn<T, Object> fn, String value) {
        return this.orLike(Reflections.fnToFieldName(fn), value);
    }

    public HTCriteria<T> orNotLike(String property, String value) {
        this.criteria.getCriterions().add(new Sqls.Criterion(property, value, "not like", "or"));
        return this;
    }

    public HTCriteria<T> orNotLike(Fn<T, Object> fn, String value) {
        return this.orNotLike(Reflections.fnToFieldName(fn), value);
    }

    /**
     * 升序
     *
     * @param fn
     * @return
     */
    public HTCriteria<T> orderAsc(Fn<T, Object> fn) {
        return order(Reflections.fnToFieldName(fn), ORDER.ASC);
    }

    /**
     * 降序
     *
     * @param fn
     * @return
     */
    public HTCriteria<T> orderDesc(Fn<T, Object> fn) {
        return order(Reflections.fnToFieldName(fn), ORDER.DESC);
    }

    /**
     * 排序
     * @param property
     * @param order
     * @return
     */
    public HTCriteria<T> order(String property, HTCriteria.ORDER order) {
        orderList.add(new HashMap<String, String>() {
            {
                put(property, order.toString());
            }
        });
        return this;
    }

    enum ORDER {
        ASC, DESC
    }

    @Override
    public Sqls.Criteria getCriteria() {
        return criteria;
    }

    /**
     * 转换成example
     *
     * @return
     */
    public Example example() {

        Example.Builder where = new Example.Builder(clazz).where(this);
        if (orderList != null && !orderList.isEmpty()) {
            for (Map<String, String> map : orderList) {
                map.forEach((k, v) -> {
                    if (v.equals(ORDER.ASC.name())){
                        where.orderByAsc(k);
                    } else if (v.equals(ORDER.DESC.name())) {
                        where.orderByDesc(k);
                    }
                });
            }
        }
        return where.build();
    }
}
