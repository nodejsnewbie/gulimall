package com.atguigu.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadPoolExecutorTest {
    public static void main(String[] args) {
        /**
         * int corePoolSize 核心线程数一直存在,线程池创建好以后就准备就绪的线程数量，就等待接收去异步执行
         * int maximumPoolSize 最大线程数量
         * long keepAliveTime 存活时间，当前运行的线程数量大于核心数量。释放空闲的线程（maximumPoolSize - corePoolSize ）。
         * TimeUnit unit 存活时间的单位
         * BlockingQueue<Runnable> workQueue 阻塞队列。如果任务有很多，就会将多的任务放到队列里面。
         *                                   只要有线程空闲，就会去队列里面取新的任务继续执行。
         * ThreadFactory threadFactory 创建线程的工厂
         * RejectedExecutionHandler handler 如果队列满了，按照指定的拒绝策略执行任务。
         *
         * 工作顺序：
         * 1.线程池创建，准备好core数量的核心线程，准备接受任务
         * 2.新的任务进来，用core准备好的空闲线程执行。
         *  1）core满了，就讲再进来的任务放入阻塞队列中。空闲的core就会自己去阻塞队队列获取任务执行
         *  2）阻塞队列满了，就执行开新线程执行，最大只能开大 max 指定的数量
         *  3）max都执行好了。max-core数量空闲的线程会在keepAliveTime指定的时间后自动销毁。最终保持到core大小
         *  4）如果线程开到了max的数量。还有新任务进来，就会使用reject指定的拒绝策略进行处理
         * 3.所有的线程创建都是由指定的factory创建的
         *
         * 一个线程池 core 7,max 20 ,queue 50,100并发进来是怎么分配的。
         * 7个会立即执行，50个进入到队列里面，再开13个进行执行，剩下的30个使用拒绝策略执行。
         * 拒绝策略：
         * 1、不抛弃策略，直接调用runnable里面的run方法，同步执行。
         * 2. 抛弃旧的执行任务
         * 3. 抛弃新的执行任务并且抛出异常
         * 4. 抛弃新的执行任务不抛出异常
         *
         * 常见的线程池：
         * 1. Executors.newCachedThreadPool()
         *    创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新创建线程
         * 2. Executors.newFixedThreadPool()
         *    创建一个定长线程池，可控制线程最大并发数量，超出的线程会在队列中等待。
         * 3. Executors.newScheduledThreadPool()
         *    创建一个定长线程池，支持定时及周期性任务执行
         * 4. Executors.newSingleThreadExecutor()
         *    创建一个单线程化的线程池，它只会用唯一的工作线程执行任务
         *
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 200
                , 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100000), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

    }
}
