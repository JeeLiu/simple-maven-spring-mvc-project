package com.sheldontalk.www.dao.common;

import com.sheldontalk.www.util.cache.ICacheAdaptor;
import com.sheldontalk.www.util.cache.ICacheAdaptorFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CachableGenericHibernateDao<T, PK extends Serializable> extends
        GenericHibernateDao<T, PK> {

    private ICacheAdaptorFactory cacheAdaptorFactory;

    protected long getElementTimeToLive() {
        return 60 * 60 / 2;
    }

    protected long getListRsTimeToLive() {
        return 60 * 60 / 4;
    }

    @Autowired
    public void initCacheAdaptorFactory(
            OutCacheAdaptorFactoryImp outCacheAdaptorFactoryImp) {
        this.cacheAdaptorFactory = outCacheAdaptorFactoryImp;
    }

    @Override
    public PK insert(T entity) throws DataAccessException {
        PK pk = super.insert(entity);

        ICacheAdaptor cacheAdaptor = this.getCacheAdaptor();
        cacheAdaptor.put(pk.hashCode(), entity, this.getElementTimeToLive());
        return pk;
    }

    @Override
    public int deleteById(PK id) throws DataAccessException {
        int count = super.deleteById(id);

        ICacheAdaptor cacheAdaptor = this.getCacheAdaptor();
        cacheAdaptor.delete(id.hashCode());
        return count;
    }

    @Override
    public void delete(T entity) throws DataAccessException {
        super.delete(entity);

        this.cacheAdaptorFactory.removeCache(super.getPersistentClass()
                .getName());
    }

    @Override
    public int deleteAll() throws DataAccessException {
        int count = super.deleteAll();
        this.cacheAdaptorFactory.removeCache(super.getPersistentClass()
                .getName());
        return count;
    }

    @Override
    public void update(T entity) throws DataAccessException {
        super.update(entity);
        Object pkValue = super.getPrimaryKeyValue(entity);
        this.cacheAdaptorFactory.removeCache(super.getPersistentClass()
                .getName());
        this.getCacheAdaptor().put(pkValue.hashCode(), entity,
                this.getElementTimeToLive());
    }

    @Override
    public void saveOrUpdate(T entity) throws DataAccessException {
        super.saveOrUpdate(entity);
        ICacheAdaptor cacheAdaptor = this.getCacheAdaptor();
        Object pk = this.getPrimaryKeyValue(entity);
        this.cacheAdaptorFactory.removeCache(super.getPersistentClass()
                .getName());
        cacheAdaptor.put(pk.hashCode(), entity, this.getElementTimeToLive());
    }

    @Override
    public T get(PK id) throws DataAccessException {
        Object o = this.getCacheAdaptor().get(id, this.getPersistentClass());
        if (o != null)
            return (T) o;

        T ret = super.get(id);

        this.getCacheAdaptor().put(id.hashCode(), ret, this.getListRsTimeToLive());
        return ret;
    }

    @Override
    public List<T> findBySql(String sql) {
        int key = sql.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findBySql(sql);

        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> findByNamedParam(String query, String[] named,
                                    Object[] values) throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append(query);
        if (named != null) {
            for (int i = 0; i < named.length; i++) {
                sb.append(named[i]).append(values[i]);
            }
        }

        int key = sb.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findByNamedParam(query, named, values);

        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> findByCriterion(Criterion... criterions)
            throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        if (criterions == null)
            return null;

        for (Criterion c : criterions) {
            sb.append(c);
        }

        int key = sb.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findByCriterion(criterions);

        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> findByDetachedCriteria(DetachedCriteria detachedCriteria) {
        if (logger.isDebugEnabled())
            logger.debug("11111111");

        int key = detachedCriteria.toString().hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findByDetachedCriteria(detachedCriteria);

        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> findByExample(T entity, String[] property)
            throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append(entity.toString());
        if (property != null) {
            for (String p : property)
                sb.append(p);
        }

        int key = sb.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findByExample(entity, property);

        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> findPageByDetachedCriteria(int curPage, int pageSize,
                                              DetachedCriteria detachedCriteria) throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append(curPage).append(pageSize);
        sb.append(detachedCriteria.toString());

        int key = sb.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findPageByDetachedCriteria(curPage, pageSize,
                detachedCriteria);

        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> getAll() {
        return super.getAll();
    }

    @Override
    public List<T> findByProperties(DetachedCriteria criteria,
                                    int... startIdxAndCount) throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append(criteria.toString());
        for (int i : startIdxAndCount) {
            sb.append(i);
        }

        int key = sb.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findByProperties(criteria, startIdxAndCount);
        keepListRs(key, rets);
        return rets;
    }

    @Override
    public List<T> findPaged(String queryString, int rowStartIdx, int rowCount,
                             Object... values) throws DataAccessException {
        StringBuilder sb = new StringBuilder();
        sb.append(queryString);
        sb.append(rowStartIdx).append(rowCount);
        if (values != null) {
            for (Object obj : values)
                sb.append(obj);
        }

        int key = sb.hashCode();
        Object o = this.getCacheAdaptor().get(key, List.class);
        if (o != null)
            return getListRs((List<T>) o);

        List<T> rets = super.findPaged(queryString, rowStartIdx, rowCount,
                values);

        keepListRs(key, rets);
        return rets;
    }

    protected void keepListRs(int key, List<T> rets) {
        if (rets == null || rets.size() == 0)
            return;

        List<PK> ids = new ArrayList<PK>();
        for (T obj : rets) {
            PK pkValue = this.getPrimaryKeyValue(obj);
            this.getCacheAdaptor().put(pkValue, obj,
                    this.getElementTimeToLive());
            ids.add(pkValue);
        }

        this.getCacheAdaptor().put(key, ids, this.getListRsTimeToLive());
    }

    protected List<T> getListRs(List<T> listRs) {
        if (listRs == null)
            return null;

        List<Object> retRs = new ArrayList<Object>();
        for (T obj : (List<T>) listRs) {
            retRs.add(this.getCacheAdaptor().get(obj, this.getPersistentClass()));
        }
        return (List<T>) retRs;
    }

    protected ICacheAdaptor getCacheAdaptor() {
        return cacheAdaptorFactory.getCacheAdaptor(super.getPersistentClass()
                .getSimpleName());
    }

    public ICacheAdaptorFactory getCacheAdaptorFactory() {
        return cacheAdaptorFactory;
    }

}
