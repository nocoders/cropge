package com.cropge.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TokenCatch {
    private static Logger logger= LoggerFactory.getLogger(TokenCatch.class);
    public static final String  TOKEN_PREFIX="token_";
    public  static LoadingCache<String,String>localcache=
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
//                默认的数据加载实现，当调用get取值时，如果key没有对应的值，就调用这个方法加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localcache.put(key,value);
    }
    public static String getKey(String key){
        String value=null;
        try {
            value=localcache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error");
        }
        return null;
    }
}
