package com.dahua.ferryman.core.netty.processor;

import com.dahua.ferryman.core.context.HttpRequestWrapper;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:31
 * 处理Netty核心逻辑的执行器接口定义
 */
public interface NettyProcessor {

    void process(HttpRequestWrapper httpRequestWrapper) throws Exception;

    void start();

    void shutdown();
}
