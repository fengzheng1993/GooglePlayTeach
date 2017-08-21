package com.fz.googleplayteach.http.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 冯政 on 2017/7/4.
 * 线程池管理者（单例模式）
 */

public class ThreadManager {
    private static ThreadPool mThreadPool;
    public static ThreadPool getInstance(){
        if (mThreadPool==null){
            synchronized (ThreadManager.class){
                if (mThreadPool==null){
                    mThreadPool=new ThreadPool(10,10,1L);
                }
            }
        }
        return mThreadPool;
    }
    //线程池
    public static class ThreadPool{
        private int corePoolSize;//核心线程数
        private int maximumPoolSize;//最大线程数
        private long keepAliveTime;//线程休眠时间
        private ThreadPoolExecutor executor;

        private ThreadPool(int corePoolSize,int maximumPoolSize,long keepAliveTime){
            this.corePoolSize = corePoolSize;
            this.keepAliveTime=keepAliveTime;
            this.maximumPoolSize=maximumPoolSize;
        }
        public void execute(Runnable r){
            if (executor==null){
                executor= new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,
                        TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
            }
            //执行一个Runnable对象，具体运行时机线程池说的算
            executor.execute(r);
        }

        //取消任务
        public void cancel(Runnable r){
            if (executor!=null){
                executor.getQueue().remove(r);//从任务对列中移除任务，如果任务还没开始正在等待，可以通过此方法移除
                //如果任务已经开始运行，需要在run方法中进行中断
            }
        }
    }
}
