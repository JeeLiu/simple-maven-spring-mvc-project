package com.sheldontalk.www.dao.common;

import com.sheldontalk.www.util.cache.ICacheAdaptor;
import com.sheldontalk.www.util.cache.ICacheAdaptorFactory;
import com.sheldontalk.www.util.cache.OutCacheAdaptorImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OutCacheAdaptorFactoryImp implements ICacheAdaptorFactory {
    private static Logger logger = LoggerFactory
            .getLogger(OutCacheAdaptorFactoryImp.class);

    /**
     * 缓存服务器基础URL
     */
//    @Value("${cache.server}")
    private URL srvBase;

    private Map<String, OutCacheAdaptorImp> adaptorHolder = new HashMap<String, OutCacheAdaptorImp>();

    public OutCacheAdaptorFactoryImp() {
    }

    public OutCacheAdaptorFactoryImp(URL srvBase) {
        this.srvBase = srvBase;
    }

    @Override
    public ICacheAdaptor getCacheAdaptor(String name) {
        if (adaptorHolder.containsKey(name))
            return adaptorHolder.get(name);

        OutCacheAdaptorImp adaptorImp = null;
        try {
            adaptorImp = this.getCache(name);
        } catch (IOException ex) {
            logger.error("get cache error.", ex);
        }

        if (adaptorImp == null) {
            try {
                adaptorImp = this.createCache(name);
            } catch (IOException ex) {
                logger.error("create cache error.", ex);
            }
        }

        return adaptorImp;
    }

    protected OutCacheAdaptorImp createCache(String name) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(srvBase.toString()).append("/").append(name);

        int status = -1;
        URL u = new URL(sb.toString());

        HttpURLConnection urlConnection = (HttpURLConnection) u
                .openConnection();
        urlConnection.setRequestMethod("PUT");
        status = urlConnection.getResponseCode();
        urlConnection.disconnect();

        if (201 == status) {
            OutCacheAdaptorImp adaptorImp = new OutCacheAdaptorImp(
                    this.srvBase, name);
            adaptorHolder.put(name, adaptorImp);
            return adaptorImp;
        }

        logger.error("Http status:{}. response:{}", status,
                urlConnection.getResponseMessage());
        return null;
    }

    protected OutCacheAdaptorImp getCache(String name) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(srvBase.toString()).append("/").append(name);

        URL u = new URL(sb.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) u
                .openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        int status = urlConnection.getResponseCode();
        String response = urlConnection.getResponseMessage();

        if (urlConnection != null)
            urlConnection.disconnect();

        if (200 == status) {
//			if (logger.isDebugEnabled())
//				logger.debug("response:{}", response);

            OutCacheAdaptorImp adaptorImp = new OutCacheAdaptorImp(srvBase,
                    name);
            return adaptorImp;
        }

        logger.info("cache not found. status:{}. response:{}", status, response);
        return null;
    }

    @Override
    public void removeCache(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(srvBase.toString()).append("/").append(name);

        int status = 0;
        String response = null;
        try {
            URL u = new URL(sb.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) u
                    .openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.connect();

            status = urlConnection.getResponseCode();
            response = urlConnection.getResponseMessage();

            if (urlConnection != null)
                urlConnection.disconnect();
        } catch (IOException ioEx) {
            logger.error("delete cache {} error", name, ioEx);
            return;
        }

        if (200 == status) {
//			if (logger.isDebugEnabled())
//				logger.debug("response:{}", response);
        }

        logger.info("cache not found. status:{}. response:{}", status, response);
    }

    public URL getSrvBase() {
        return srvBase;
    }

    public void setSrvBase(URL srvBase) {
        this.srvBase = srvBase;
    }

}
