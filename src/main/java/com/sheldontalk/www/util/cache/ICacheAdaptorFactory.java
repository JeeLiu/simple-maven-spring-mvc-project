package com.sheldontalk.www.util.cache;

public interface ICacheAdaptorFactory {
    public ICacheAdaptor getCacheAdaptor(String name);

    public void removeCache(String name);
}
