package com.sheldontalk.www.dao.common;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IGenericHibernateDao<T, PK extends Serializable> {

    /**
     * 新增
     *
     * @param entity
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public PK insert(final T entity) throws DataAccessException;

    /**
     * 根据主键删除记录
     *
     * @param id
     *            ：记录ID
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public int deleteById(final PK id) throws DataAccessException;

    /**
     * 根据实体删除记录
     *
     * @param entity
     * @throws org.springframework.dao.DataAccessException
     */
    public void delete(final T entity) throws DataAccessException;

    /**
     * 删除全部记录
     *
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public int deleteAll() throws DataAccessException;

    /**
     * 更新
     *
     * @param entity
     * @throws org.springframework.dao.DataAccessException
     */
    public void update(final T entity) throws DataAccessException;

    /**
     * 新增或更新记录
     *
     * @param entity
     * @throws org.springframework.dao.DataAccessException
     */
    public void saveOrUpdate(final T entity) throws DataAccessException;

    /**
     * 根据主健获取实体
     *
     * @param id
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public T get(final PK id) throws DataAccessException;

    /**
     *
     * @return
     */
    public List<T> getAll();

    public List<T> getAllByPage(int pageNum, int pageSize, Map<String, Boolean> orderPair);

    public List<T> getPageByDetachedCriteria(final int curPage,
                                             final int pageSize, final DetachedCriteria detachedCriteria)
            throws DataAccessException;

    /**
     * 使用带命名参数的命名HSQL语句检索数据
     *
     * @param query
     *            ：查询语句
     * @param named
     *            ：参数集体
     * @param values
     *            ：值集合
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByNamedParam(final String query, final String[] named,
                                    final Object[] values) throws DataAccessException;

    /**
     * 使用指定的检索标准检索数据，返回记录
     *
     * @param criterions
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByCriterion(final Criterion... criterions)
            throws DataAccessException;

    /**
     * 根据查询条件返回记录
     *
     * @param detachedCriterion
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByDetachedCriteria(
            final DetachedCriteria detachedCriteria) throws DataAccessException;

    /**
     * 根据查询条件返回记录
     *
     * @param entity
     * @param property
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByExample(final T entity, final String[] property)
            throws DataAccessException;

    /**
     * 根据条件分页查询记录
     *
     * @param curPage
     *            ：开始索引
     * @param pageSize
     *            ：最大记录数
     * @param detachedCriterion
     *            ：查询条件
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findPageByDetachedCriteria(final int curPage,
                                              final int pageSize, final DetachedCriteria detachedCriteria)
            throws DataAccessException;

    /**
     * 根据查询条件取总记录数
     *
     * @param detachedCriteria
     *            ：查询条件
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public long countByDetachedCriteria(final DetachedCriteria detachedCriteria)
            throws DataAccessException;

    /**
     *
     * @param detachedCriteria
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> executeDetachedCriteria(
            final DetachedCriteria detachedCriteria) throws DataAccessException;

    /**
     *
     * @param queryString
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> executeQuery(final String queryString)
            throws DataAccessException;

    /**
     *
     * @param entity
     * @return
     */
    public T merge(final T entity);

}
