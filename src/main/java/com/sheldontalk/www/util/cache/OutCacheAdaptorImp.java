package com.sheldontalk.www.util.cache;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OutCacheAdaptorImp implements ICacheAdaptor {
    private static Logger logger = LoggerFactory
            .getLogger(OutCacheAdaptorImp.class);

    private URL srvBase;

    /**
     * cache name
     */
    private String name;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public OutCacheAdaptorImp(URL srvBase, String name) {
        this.setSrvBase(srvBase);
        this.setName(name);
    }

    @Override
    public void put(Object key, Object value, long timeToLive) {
        if (value == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(srvBase.toString()).append("/").append(name).append("/")
                .append(key);

        int status = -1;
        String respMsg = null;
        HttpURLConnection urlConnection = null;

        try {
            URL u = new URL(sb.toString());
            urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestProperty("ehcacheTimeToLiveSeconds",
                    String.valueOf(timeToLive));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.connect();

            OutputStream os = urlConnection.getOutputStream();
            objectMapper.writeValue(os, value);
            os.flush();

            status = urlConnection.getResponseCode();
            if (status != 201) {
                respMsg = urlConnection.getResponseMessage();
                logger.error("Http status:{}, respMsg:{}", status, respMsg);
            }
        } catch (IOException ioEx) {
            logger.error("Put element {} error.", key, ioEx);
        }

        if (urlConnection != null)
            urlConnection.disconnect();
    }

    @Override
    public Object get(Object key, Class<?> clazz) {
        if (key == null)
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(srvBase.toString()).append("/").append(name).append("/")
                .append(key);

        Object retValue = null;
        int status = -1;
        String respMsg = null;
        HttpURLConnection urlConnection = null;
        InputStream is = null;
        try {
            URL url = new URL(sb.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            status = urlConnection.getResponseCode();
            if (status == 404) {
                logger.info("Value not found by the key:{}.", key);
            } else if (status != 200) {
                respMsg = urlConnection.getResponseMessage();
                logger.error("Http status:{}, respMsg:{}", status, respMsg);
            } else {
                is = urlConnection.getInputStream();
                retValue = objectMapper.readValue(is, clazz);
            }

            if (is != null)
                is.close();
        } catch (IOException ioEx) {
            logger.error("Get element {} error.", key, ioEx);
        }

        if (urlConnection != null)
            urlConnection.disconnect();

        return retValue;
    }

    public void delete(Object key) {
        if (key == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append(srvBase.toString()).append("/").append(name).append("/")
                .append(key);

        int status = -1;
        String respMsg = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(sb.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            urlConnection.connect();

            InputStream is = urlConnection.getInputStream();
            status = urlConnection.getResponseCode();
            if (status != 200) {
                respMsg = urlConnection.getResponseMessage();
                logger.error("Http status:{}, respMsg:{}", status, respMsg);
            }

            if (is != null)
                is.close();
        } catch (IOException ioEx) {
            logger.error("Delete element {} error.", key, ioEx);
        }

        if (urlConnection != null)
            urlConnection.disconnect();
    }

    @Override
    public List<Object> getBatch(List<Object> keys) {
        return null;
    }

    @Override
    public void putBatch(List<Object> objects) {

    }

    public URL getSrvBase() {
        return srvBase;
    }

    public void setSrvBase(URL srvBase) {
        this.srvBase = srvBase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
