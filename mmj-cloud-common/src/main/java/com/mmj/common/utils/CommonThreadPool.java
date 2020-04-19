package com.mmj.common.utils;


import java.io.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommonThreadPool {

    public static String key = "COMMON:DELAYQUEUE:";

    private Integer corePoolSize = 10;     //核心线程池大小

    private Integer maximumPoolSize = 20;  //最大线程池大小

    private Long keepAliveTime = 2L;   //线程最大空闲时间

    private TimeUnit timeUnit = TimeUnit.SECONDS; //时间单位秒

    private DelayQueue queue = new DelayQueue<CommonDelayQueue>();   //线程等待队列

    public ThreadPoolExecutor executor;

    /**
     * @param corePoolSize 核心线程池大小
     * @param maximumPoolSize 最大线程池大小
     * @param keepAliveTime 线程最大空闲时间(单位秒)
     */
    public CommonThreadPool(Integer corePoolSize, Integer maximumPoolSize,Long keepAliveTime){
        if (corePoolSize != null && corePoolSize != 0) {
            this.corePoolSize = corePoolSize;
        }
        if (maximumPoolSize != null && maximumPoolSize != 0) {
            this.maximumPoolSize = maximumPoolSize;
        }
        if (keepAliveTime != null && keepAliveTime != 0) {
            this.keepAliveTime = keepAliveTime;
        }
        this.executor =  new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, queue);
        executor.prestartAllCoreThreads();
    }

    /**
     * 无参构造方法
     */
    public CommonThreadPool(){
        this.executor =  new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, queue);
        executor.prestartAllCoreThreads();
    }

    public static byte[] serialize(Object obj) {
        byte[] bytes = null;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(obj);
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Object deSerialize(byte[] bytes) {
        Object obj = null;
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais)
        ) {
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
