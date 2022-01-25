package com.dahua.ferryman.common.queue.flusher;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/25 下午7:55
 */
public interface Flusher<E> {

    void add(E event);

    void add(@SuppressWarnings("unchecked") E... event);

    boolean isShutdown();

    void start();

    void shutdown();

}
