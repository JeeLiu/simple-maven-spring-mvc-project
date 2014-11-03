package com.sheldontalk.www.util.cache;

import java.util.List;

public interface ICacheAdaptor {

    public void put(Object key, Object value, long timeToLive);

    public Object get(Object key, Class<?> clazz);

    public void delete(Object key);

    public List<Object> getBatch(List<Object> keys);

    public void putBatch(List<Object> objects);
}
