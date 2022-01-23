package com.dahua.ferryman.discovery.api;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:08
 */
public interface RegistryService  extends Registry{

    /**
     * 添加一堆的监听事件
     */
    void addWatcherListeners(String superPath, Notify notify);

    /**
     * 初始化注册服务
     */
    void initialized(String registryAddress);

}
