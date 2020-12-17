package com.dalididilo.thread;


import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * 阻塞队列
 * 线程安全操作队列，生成-消费者模式。
 * @author dalididilo
 * @date 2020-12-14 14:16:55
 */
public class BlockingQueueTest {

    /**
     * 数组有界阻塞队列
     * 底层数据结构 - 数据。
     * int capacity 初始化大小
     * boolean fair true公平、默认false非公平锁
     */
    private ArrayBlockingQueue arrayBlockingQueue
            = new ArrayBlockingQueue(10);
    /**
     * 链表有界阻塞队列
     * 底层数据结构 - 链表
     * 默认大小 Integer.MAX_VALUE，可指定 int capacity 大小。
     */
    private LinkedBlockingQueue linkedBlockingQueue
            = new LinkedBlockingQueue();

    /**
     * 优先级无界队列。
     */
    private PriorityBlockingQueue priorityBlockingQueue
            = new PriorityBlockingQueue();
    /**
     * 同步队列。
     * 一个put对应一个take操作。
     */
    private SynchronousQueue synchronousQueue
            = new SynchronousQueue();

    /**
     * 延迟队列元素
     */
    static class DelayNode implements Delayed{
        private long time;
        private String name;

        public DelayNode(String name,long time,TimeUnit unit) {
            this.name = name;
            // 当前时间 + 延时时间。
            this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time):0);
        }

        /**
         * 定义剩余到期时间。
         * @param unit
         * @return
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return time - System.currentTimeMillis();
        }

        /**
         * 优先级队列使用的排序方式
         * 要始终保证延迟低的元素在靠前位置
         * @param o
         * @return
         */
        @Override
        public int compareTo(Delayed o) {
            DelayNode o1 = (DelayNode) o;
            long diff = this.time - o1.time;
            if (diff <= 0) {
                return -1;
            }else{
                return 1;
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"time\":")
                    .append(time);
            sb.append(",\"name\":\"")
                    .append(name).append('\"');
            sb.append('}');
            return sb.toString();
        }
    }

    @Test
    public void delayQueueTest(){

        /**
         * 延迟队列
         */
        DelayQueue<DelayNode> delayQueue = new DelayQueue();
        delayQueue.put(new DelayNode("node1",1,TimeUnit.SECONDS));
        delayQueue.put(new DelayNode("node2",2,TimeUnit.SECONDS));
        delayQueue.put(new DelayNode("node3",2,TimeUnit.SECONDS));
        System.out.println(String.format("BeginTime : %s , size : %d"
                , LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),delayQueue.size()));
        for (int i = 0; i < 3; i++) {
            try {
                DelayNode take = delayQueue.take();
                System.out.println(String.format("name:%s ,time:%s",take.name
                        ,LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 生产者-消费者模型。
     */
    static class ProducerAndConsumerModel {
        private Integer queueSize = 10;
        ArrayBlockingQueue<Integer> queue
                = new ArrayBlockingQueue<>(queueSize);
        public static void main(String[] args){

            ProducerAndConsumerModel model = new ProducerAndConsumerModel();
            ProducerAndConsumerModel.Producer producer = model.new Producer();
            ProducerAndConsumerModel.Consumer consumer = model.new Consumer();
            new Thread(producer).start();
            new Thread(consumer).start();
        }

        /**
         * 生产
         */
        class Producer implements Runnable{

            @Override
            public void run() {
                produce();
            }

            private void produce() {
                for (;;){
                    try {
                        queue.put(1);
                        System.out.println("向队列插入一个元素，队列剩余空间:" + (queue.size() - 1));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 消费
         */
        class Consumer implements Runnable{

            @Override
            public void run() {
                consume();
            }

            private void consume() {
                for(;;){
                    try {
                        queue.take();
                        System.out.println("从队列取走一个元素，队列剩余" + queue.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
