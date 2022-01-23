package com.dahua.ferryman.core.netty;

import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.lifecycle.LifeCycle;
import com.dahua.ferryman.core.netty.processor.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:29
 */
@Slf4j
public class NettyHttpServer implements LifeCycle {

    private FerrymanConfig ferrymanConfig;

    private int port = 8801;

    private NettyProcessor nettyProcessor;

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroupBoss;

    private EventLoopGroup eventLoopGroupWork;

    public NettyHttpServer(FerrymanConfig ferrymanConfig, NettyProcessor nettyProcessor) {
        this.ferrymanConfig = ferrymanConfig;
        this.nettyProcessor = nettyProcessor;
        if(ferrymanConfig.getPort() > 0 && ferrymanConfig.getPort() < 65535) {
            this.port = ferrymanConfig.getPort();
        }
        //	初始化NettyHttpServer
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
