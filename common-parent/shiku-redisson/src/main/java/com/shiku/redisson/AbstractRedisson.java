package com.shiku.redisson;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public abstract class AbstractRedisson {

         public abstract RedissonClient getRedissonClient();

        public String buildRedisKey(String keyFormat,Object ... params ){
            return String.format(keyFormat,params);
        }
        public <T> T getBucket(Class<T> tClass,String key){
            RBucket<T> bucket = getRedissonClient().getBucket(key);
            return bucket.get();
        }
        public <T> T getBucket(Class<T> tClass,String keyFormat,Object ... params ){
            String key=buildRedisKey(keyFormat,params);
            RBucket<T> bucket = getRedissonClient().getBucket(key);
            return bucket.get();
        }

        public <T> List<T> getList(Class<T> tClass,String key){
            RList<T> bucket = getRedissonClient().getList(key);
            return bucket.readAll();
        }
        public <T> List<T> getList(Class<T> tClass, String keyFormat, Object ... params ){
            String key=buildRedisKey(keyFormat,params);
            RList<T> bucket = getRedissonClient().getList(key);
            return bucket.readAll();
        }

     public boolean deleteBucket(String keyFormat,Object ... params ){
        String key=buildRedisKey(keyFormat,params);
        RBucket<Object> bucket = getRedissonClient().getBucket(key);
        return bucket.delete();
    }


    public boolean setBucket(String key,Object obj){
        RBucket<Object> bucket = getRedissonClient().getBucket(key);
        bucket.set(obj);
        return true;
    }

    public boolean setBucket(String key, Object obj, long time){
       return setBucket(key,obj,time,TimeUnit.SECONDS);
    }
    public boolean setBucket(String key, Object obj, long time, TimeUnit unit){
        RBucket<Object> bucket = getRedissonClient().getBucket(key);
        bucket.set(obj,time,unit);
        return true;
    }

    public boolean getLock(String lockKey,long time) {
        try {
             RLock lock = getRedissonClient().getLock(lockKey);

            return lock.tryLock(time,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }catch (Exception e){
            return false;
        }

    }

    public Lock getLock(String keyFormat, long time, Object ... params ) {
        String key=buildRedisKey(keyFormat,params);
        return getRedissonClient().getLock(key);
    }
    public boolean getLockResult(String keyFormat,long time,Object ... params ) {
        try {
            String key=buildRedisKey(keyFormat,params);
            RLock lock = getRedissonClient().getLock(key);

            return lock.tryLock(time,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }catch (Exception e){
            return false;
        }

    }
    public void unLock(String lockKey) {
        try {
            RLock lock = getRedissonClient().getLock(lockKey);
            lock.unlock();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
