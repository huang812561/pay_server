package com.ykc.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @Description: 线程池工具类
 * @Author: zhutao
 * @Date: 2020/2/24
 */
@Configuration
public class ThreadPoolUtil {

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;

    /**
     * KEEP_ALIVE_TIME
     */
    private static final long KEEP_ALIVE_TIME = 0;

    /**
     * 延时队列
     */
    private static final BlockingQueue<Runnable> QUEUE = new ArrayBlockingQueue<>(2000);

    @Bean("executorService")
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, QUEUE, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
