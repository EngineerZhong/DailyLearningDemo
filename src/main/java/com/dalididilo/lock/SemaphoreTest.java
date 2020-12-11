package com.dalididilo.lock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 信号量锁、互斥锁。
 * @author dalididilo
 * @date 2020-11-25 09:54:45
 * @description
 * AQS
 */
public class SemaphoreTest {
    /**
     * 信号量锁，permits 信号量，fair 公平/非公平锁
     * 默认构造函数是非公平锁。
     */
    public static void main(String[] args){
        final Semaphore semaphore = new Semaphore(2,false);
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("pool-%d").build();
        ExecutorService execPool = new ThreadPoolExecutor(20,200,0L,TimeUnit.MILLISECONDS
                , new LinkedBlockingDeque<Runnable>(1024),factory,new ThreadPoolExecutor.AbortPolicy());
        for (int i = 0;i<=10;i++) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println(Thread.currentThread().getName() + "测试");
                        Thread.sleep(1000L);
                    }catch (Exception e){

                    }finally {
                        semaphore.release();
                    }
                }
            };
            execPool.execute(run);
        }
        execPool.shutdown();
    }

}
