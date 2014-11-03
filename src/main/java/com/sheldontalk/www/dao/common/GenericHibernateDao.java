package com.sheldontalk.www.dao.common;


import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"unchecked", "rawtypes"})
public class GenericHibernateDao<T, PK extends Serializable> extends
        HibernateDaoSupport implements IGenericHibernateDao<T, PK> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<T> persistentClass;

    private String className;

    public GenericHibernateDao() {
        Class<?> clazz = this.getClass();

        for (; ; ) {
            // Class<?> superKlass = clazz.getSuperclass();
            Type genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass == null)
                return;

            if (genericSuperclass instanceof ParameterizedType) {
                // Type genericSuperclass = clazz.getGenericSuperclass();
                Type[] types = ((ParameterizedType) genericSuperclass)
                        .getActualTypeArguments();
                this.persistentClass = (Class<T>) types[0];
                break;
            }

            clazz = clazz.getSuperclass();
        }

        className = persistentClass.getSimpleName();
    }

    @Autowired
    public void initSessionFactory(SessionFactory sessionFactory) {
        setSessionFactory(sessionFactory);
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public PK insert(final T entity) throws DataAccessException {
        return (PK) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Serializable id = session.save(entity);
                return id;
            }

        });
    }

    public void saveBatch(final List<T> entities) throws DataAccessException {
        if (entities == null)
            return;

        Session session = this.getHibernateTemplate().getSessionFactory()
                .openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (T entity : entities) {
            session.save(entity);
            i++;
            if (i % 40 == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();

        tx.commit();
        session.close();
    }

    /**
     * 根据主键删除记录
     *
     * @param id ：记录ID
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public int deleteById(final PK id) throws DataAccessException {
        return (Integer) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {

                        int affectedRecords = session
                                .createQuery(
                                        "delete from " + className
                                                + " where id = ?"
                                )
                                .setParameter(0, id).executeUpdate();
                        return affectedRecords;
                    }
                });
    }

    /**
     * 根据实体删除记录
     *
     * @param entity
     * @throws org.springframework.dao.DataAccessException
     */
    public void delete(final T entity) throws DataAccessException {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                session.delete(entity);
                return null;
            }
        });

    }

    /**
     * 删除全部记录
     *
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public int deleteAll() throws DataAccessException {
        return getHibernateTemplate().bulkUpdate("delete from " + className);
    }

    /**
     * 更新
     *
     * @param entity
     * @throws org.springframework.dao.DataAccessException
     */
    public void update(final T entity) throws DataAccessException {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                // session = getSessionFactory().getCurrentSession();
                session.update(entity);
                return null;
            }
        });
    }

    /**
     * 新增或更新记录
     *
     * @param entity
     * @throws org.springframework.dao.DataAccessException
     */
    public void saveOrUpdate(final T entity) throws DataAccessException {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                session.saveOrUpdate(entity);
                return null;
            }
        });
    }

    /**
     * 根据主健获取实体
     *
     * @param id
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public T get(final PK id) throws DataAccessException {
        return (T) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Object object = session.get(getPersistentClass(), id);

                return object;
            }
        });
    }

    @Override
    public List<T> getAllByPage(int pageNum, int pageSize,
                                Map<String, Boolean> orderPair) {
        DetachedCriteria detachedCriteria = DetachedCriteria
                .forClass(getPersistentClass());
        if (orderPair != null && !orderPair.isEmpty()) {
            Iterator<Map.Entry<String, Boolean>> itr = orderPair.entrySet()
                    .iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Boolean> e = itr.next();
                String field = e.getKey();
                Boolean isAsc = e.getValue();
                if (isAsc)
                    detachedCriteria.addOrder(Order.asc(field));
                else
                    detachedCriteria.addOrder(Order.desc(field));
            }
        }

        return getPageByDetachedCriteria(pageNum, pageSize, detachedCriteria);
    }

    /**
     * 根据条件分页查询记录
     *
     * @param curPage           ：开始索引
     * @param pageSize          ：最大记录数
     * @param detachedCriterion ：查询条件
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public List<T> getPageByDetachedCriteria(final int curPage,
                                             final int pageSize, final DetachedCriteria detachedCriteria)
            throws DataAccessException {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria_ = detachedCriteria
                        .getExecutableCriteria(session);
                criteria_.add(Restrictions.disjunction());

                int offset = 0;
                if (curPage > 0 && pageSize > 0) {
                    offset = curPage * pageSize;
                }

                criteria_.setFirstResult(offset);
                criteria_.setMaxResults(pageSize);
                return criteria_.list();
            }
        });
    }

    /**
     * 使用带命名参数的命名HSQL语句检索数据
     *
     * @param query  ：查询语句
     * @param named  ：参数集体
     * @param values ：值集合
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByNamedParam(final String query, final String[] named,
                                    final Object[] values) throws DataAccessException {
        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                List list = getHibernateTemplate().findByNamedParam(query,
                        named, values);
                return list;
            }
        });
    }

    /**
     * 使用指定的检索标准检索数据，返回记录
     *
     * @param criterions
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByCriterion(final Criterion... criterions)
            throws DataAccessException {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = session
                        .createCriteria(getPersistentClass());
                criteria.add(Restrictions.disjunction());
                for (Criterion criterion : criterions) {
                    criteria.add(criterion);
                }
                List result = criteria.list();

                return result;
            }
        });
    }

    /**
     * 根据查询条件返回记录
     *
     * @param detachedCriterion
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByDetachedCriteria(
            final DetachedCriteria detachedCriteria) {

        try {
            return getHibernateTemplate().executeFind(new HibernateCallback() {
                public Object doInHibernate(Session session)
                        throws HibernateException, SQLException {
                    Criteria criteria_ = detachedCriteria
                            .getExecutableCriteria(session);
                    criteria_.add(Restrictions.disjunction());
                    criteria_
                            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY); // 去重
                    return criteria_.list();
                }
            });
        } catch (DataAccessException e) {
            logger.error(this.persistentClass.getName(), e);
            return null;
        }
    }

    /**
     * 根据查询条件返回记录
     *
     * @param entity
     * @param property
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findByExample(final T entity, final String[] property)
            throws DataAccessException {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria = session
                        .createCriteria(getPersistentClass());

                Example example = Example.create(entity);

                if (property != null) {
                    for (String exclude : property) {
                        example.excludeProperty(exclude);
                    }
                }
                criteria.add(example);
                return criteria.list();
            }
        });
    }

    /**
     * 根据条件分页查询记录
     *
     * @param curPage           ：开始索引
     * @param pageSize          ：最大记录数
     * @param detachedCriterion ：查询条件
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findPageByDetachedCriteria(final int curPage,
                                              final int pageSize, final DetachedCriteria detachedCriteria)
            throws DataAccessException {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria_ = detachedCriteria
                        .getExecutableCriteria(session);
                criteria_.add(Restrictions.disjunction());

                int offset = 0;
                if (curPage > 0 && pageSize > 0) {
                    offset = curPage * pageSize;
                }

                criteria_.setFirstResult(offset);
                criteria_.setMaxResults(pageSize);
                return criteria_.list();
            }
        });
    }

    /**
     * 根据条件查询记录
     *
     * @param offset            ：开始索引
     * @param size              ：最大记录数
     * @param detachedCriterion ：查询条件
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public List<T> findOffsetByDetachedCriteria(final int offset,
                                                final int size, final DetachedCriteria detachedCriteria)
            throws DataAccessException {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Criteria criteria_ = detachedCriteria
                        .getExecutableCriteria(session);
                criteria_.add(Restrictions.disjunction());

                criteria_.setFirstResult(offset);
                criteria_.setMaxResults(size);
                return criteria_.list();
            }
        });
    }

    /**
     * 根据查询条件取总记录数
     *
     * @param detachedCriteria ：查询条件
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    public long countByDetachedCriteria(final DetachedCriteria detachedCriteria) {
        Long count = (Long) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        Criteria criteria = detachedCriteria
                                .getExecutableCriteria(session);
                        return criteria.setProjection(Projections.rowCount())
                                .uniqueResult();
                    }
                });

        if (null == count) {
            return 0;
        } else {
            return count.longValue();
        }
    }

    public long countBySql(final String sql) {
        Long count = (Long) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        Long count = (Long) session.createSQLQuery(sql)
                                .addScalar("count", Hibernate.LONG)
                                .uniqueResult();

                        return count;
                    }
                }
        );

        if (null == count) {
            return 0;
        } else {
            return count.longValue();
        }
    }

    public List<T> executeDetachedCriteria(
            final DetachedCriteria detachedCriteria) throws DataAccessException {

        return (List<T>) getHibernateTemplate().execute(
                new HibernateCallback() {

                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return detachedCriteria.getExecutableCriteria(session)
                                .list();
                    }
                });
    }

    public List<T> executeQuery(final String queryString)
            throws DataAccessException {

        return getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                Query query = session.createQuery(queryString);
                List list = query.list();

                return list;
            }
        });
    }

    public void insertOrUpdateBySql(final String sql, final Object[] values) {
        List list = (List) getHibernateTemplate().executeFind(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        Query query = session.createSQLQuery(sql);
                        for (int i = 0; i < values.length; i++) {
                            query.setParameter(i, values[i]);
                        }
                        query.executeUpdate();
                        Object o = null;
                        return o;
                    }
                });
    }

    public T merge(final T entity) {
        return (T) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                session.setFlushMode(FlushMode.AUTO);
                return session.merge(entity);

            }
        });
    }

    public List<T> getAll() {
        return findByCriterion();
    }

    // ///////////////////////////////////////////////////////////////////////////

    public List<T> findBySql(String sql) {
        // logger.info("sql=" + sql);

        Session session = null;
        List<T> list = null;
        try {
            session = getSession();
            list = session.createSQLQuery(sql).addEntity(persistentClass)
                    .list();

        } catch (HibernateException e) {
            logger.error(this.persistentClass.getName(), e);
            return null;
            // throw convertHibernateAccessException(e);
        } finally {
            releaseSession(session);
        }
        return list;
    }

    public List findByBaseSql(String sql) {
        List list = null;
        List baseList = null;
        List nList = null;
        Session session = null;
        try {
            session = getSession();
            list = session.createSQLQuery(sql).list();

            baseList = new ArrayList();
            for (Iterator ite = list.iterator(); ite.hasNext(); ) {

                nList = new ArrayList();
                Object obj = ite.next();

                if (obj instanceof Object[]) {
                    /* 多条记录 */
                    Object[] objs = (Object[]) obj;
                    if (objs != null) {
                        if (objs.length > 1) {
							/* 二维 */
                            for (int i = 0; i < objs.length; i++) {
                                nList.add(objs[i]);
                            }

                            baseList.add(nList);

                        } else if (objs.length > 0) {
							/* 一维 */
                            baseList.add(objs[0]);
                        }
                    }
                } else {
					/* 单条记录 */
                    if (obj != null) {

						/* 一维 */
                        baseList.add(obj);
                    }
                }
            }

        } catch (HibernateException e) {
            logger.error(this.persistentClass.getName(), e);
            return null;
        } finally {
            releaseSession(session);
        }
        return baseList;
    }

    public List<T> findByProperties(DetachedCriteria criteria,
                                    int... startIdxAndCount) throws DataAccessException {

        int firstResult = -1;
        int maxResults = -1;
        if (startIdxAndCount.length > 1) {
            firstResult = startIdxAndCount[0];
            maxResults = startIdxAndCount[1];
        }
        return super.getHibernateTemplate().findByCriteria(criteria,
                firstResult, maxResults);
    }

    public List<T> findPaged(String queryString, int rowStartIdx, int rowCount,
                             Object... values) throws DataAccessException {

        // logger.info("queryString=" + queryString);

        Session session = null;
        List<T> list = null;
        try {
            session = getSession();
            Query query = session.createQuery(queryString);
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    query.setParameter(i, values[i]);
                }
            }
            if (rowStartIdx > 0)
                query.setFirstResult(rowStartIdx);
            if (rowCount > 0)
                query.setMaxResults(rowCount);
            list = query.list();

        } catch (HibernateException e) {
            throw convertHibernateAccessException(e);
        } finally {
            releaseSession(session);
        }
        return list;
    }

    /**
     * 执行非查询sql
     *
     * @param sql
     */
    public void executeSQL(String sql) {
        final String tempsql = sql;
        this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
                    throws HibernateException {
                session.createSQLQuery(tempsql).executeUpdate();

                return null;
            }
        });
    }

    /**
     * 执行非查询sql
     *
     * @param sql
     */
    public int executeUpdateSQL(String sql) {
        Session session = this.getHibernateTemplate().getSessionFactory()
                .openSession();
        Query query = session.createQuery(sql);
        return query.executeUpdate();
    }

    /**
     * 执行普通SQL查询,返回ResultSet
     */
    // public ResultSet selectResultset(final String sql) {
    // PreparedStatement pStmt = null;
    // ResultSet rs = null;
    // SessionFactory sessionFactory = this.getHibernateTemplate()
    // .getSessionFactory();
    // Session session = sessionFactory.openSession();
    //
    // try {
    // pStmt = session.connection().prepareStatement(sql);
    // rs = pStmt.executeQuery();
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    //
    // return rs;
    // }
    public void execNativeSql(final String sql) {
        Session session = this.getHibernateTemplate().getSessionFactory()
                .openSession();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.execute();
            }
        });
    }

    public PK getPrimaryKeyValue(Object o) {
        ClassMetadata classMetadata = this.getHibernateTemplate()
                .getSessionFactory().getClassMetadata(this.persistentClass);
        String idPropName = classMetadata.getIdentifierPropertyName();
        try {
            return (PK) BeanUtils.getProperty(o, idPropName);
        } catch (IllegalAccessException e) {
            logger.error("can not access property {}.", idPropName, e);
        } catch (InvocationTargetException e) {
            logger.error("invoke target:{} error", idPropName, e);
        } catch (NoSuchMethodException e) {
            logger.error(this.persistentClass.getName(), e);
        }

        return null;
    }
}