package com.dalididilo.thread;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * 线程池
 * @author dalididilo
 * @date 2020-12-14 09:58:49
 */
public class ThreadPoolExecutorTest {

    /**
     * 线程池构建工厂。
     */
    private ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("threadPool-%d").build();
    /**
     * 线程池对象。
     */
    private ExecutorService executor = new ThreadPoolExecutor(3,3,30, TimeUnit.SECONDS
            ,new LinkedBlockingQueue<Runnable>(),factory,new ThreadPoolExecutor.CallerRunsPolicy());
    /**
     * 缓存线程池
     */
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    /**
     * 单核心线程线程池
     */
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    /**
     * 定长线程池
     */
    private ExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
    /**
     * 指定大小核心线程池。
     */
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);


    @Test
    public void threadPoolExecutor(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello ThreadPoolExecutor");
            }
        });
    }
}
