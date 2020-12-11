package com.dalididilo.lock;


import org.junit.Test;

/**
 * volatile关键字实现类似"Semaphore 信号量功能的类"
 * volatile 声明的变量，
 *      1、保证内存可见性。
 *      2、禁止volatile变量和普通变量重排序。
 * @description 一个线程对该声明变量改变时，其它线程对其值可立马可见。
 * @author dalididilo
 * @date 2020-12-11 16:54:25
 */
public class VolatileThreadTest {

    private static volatile int signal  = 0;
    private static Object lock = new Object();

    @Test
    public void testVolatileThreadSemaphoreChat(){
        new Thread(new ThreadA()).start();
        new Thread(new ThreadB()).start();
    }

    static class ThreadA implements Runnable{
        @Override
        public void run() {
            while (signal < 5){
                if (signal % 2 == 0){
                    synchronized(lock){
                        System.out.println("Thread-A:" + signal);
                        signal += 1;
                    }
                }
            }
        }
    }
    static class ThreadB implements Runnable{
        @Override
        public void run() {
            while (signal < 5){
                if (signal % 2 == 1){
                    synchronized(lock){
                        System.out.println("Thread-B:" + signal);
                        signal += 1;
                    }
                }
            }
        }
    }



}
