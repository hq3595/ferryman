package com.dahua.ferryman.core.lifecycle;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:24
 * 生命周期管理接口：
 */
public interface LifeCycle {

    void init();

    void start();

    void shutdown();
}
