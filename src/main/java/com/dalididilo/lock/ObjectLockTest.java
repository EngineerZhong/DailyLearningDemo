package com.dalididilo.lock;


import org.junit.Test;

/**
 * 对象锁通知机制。
 *  Object.notify();
 *  Object.notifyAll();
 * @author dalididilo
 * @date 2020年12月11日16:09:33
 */
public class ObjectLockTest {

    /**
     * NoneLock
     * 多线程、无锁，取决于CPU时间片轮询调度决定线程的执行先后顺序。
     */
    @Test
    public void testNoneLockThread(){
        new Thread(new ThreadA()).start();
        new Thread(new ThreadB()).start();
    }
    static class ThreadA implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
    static class ThreadB implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
    }
    /**
     * 对象锁
     * synchronized(Object){}
     * 同一时间只有一个线程持有一个锁
     */
    @Test
    public void testSynchronizedObjectLock(){
        new Thread(new ThreadD()).start();
        new Thread(new ThreadC()).start();
    }
    /**
     * 锁对象。
     */
    private static Object lock = new Object();
    static class ThreadC implements Runnable{
        @Override
        public void run() {
            synchronized(lock){
                for (int i = 0; i < 100; i++) {
                    System.out.println("ThreadC-Console:" + i);
                }
            }
        }
    }
    static class ThreadD implements Runnable{
        @Override
        public void run() {
            synchronized(lock){
                for (int i = 0; i < 100; i++) {
                    System.out.println("ThreadD-Console:" + i);
                }
            }
        }
    }

    /**
     * 线程间，等待/通知机制通信。[前提是同一个对象锁。]
     * Object类中wait()、notify()、notifyAll()
     * Object.wait()，让自己进入等待状态，此时锁被释放。
     * Object.notify()，通知之前处于等待状态的线程继续执行。
     */
    @Test
    public void testThreadNotifyAndLockWait(){
        new Thread(new ThreadE()).start();
        new Thread(new ThreadF()).start();
    }
    static class ThreadE implements Runnable{
        @Override
        public void run() {
            synchronized(lock){
                for (int i = 0; i < 100; i++) {
                    try {
                        System.out.println("ThreadE-Console:" + i);
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.notify();
            }
        }
    }
    static class ThreadF implements Runnable{
        @Override
        public void run() {
            synchronized(lock){
                for (int i = 0; i < 100; i++) {
                    try {
                        System.out.println("ThreadF-Console:" + i);
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.notify();
            }
        }
    }

}
