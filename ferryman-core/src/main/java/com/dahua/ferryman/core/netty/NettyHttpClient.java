package com.dahua.ferryman.core.netty;

import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.lifecycle.LifeCycle;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:28
 */
@Slf4j
public class NettyHttpClient implements LifeCycle {

    private FerrymanConfig ferrymanConfig;

    private EventLoopGroup eventLoopGroupWork;

    public NettyHttpClient(FerrymanConfig ferrymanConfig, EventLoopGroup eventLoopGroupWork) {
        this.ferrymanConfig = ferrymanConfig;
        this.eventLoopGroupWork = eventLoopGroupWork;
        //	在构造函数调用初始化方法
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
