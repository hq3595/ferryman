package com.dahua.ferryman.core.container;

import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.lifecycle.LifeCycle;
import com.dahua.ferryman.core.netty.NettyHttpClient;
import com.dahua.ferryman.core.netty.NettyHttpServer;
import com.dahua.ferryman.core.netty.processor.NettyCoreProcessor;
import com.dahua.ferryman.core.netty.processor.NettyProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:26
 */
@Slf4j
public class GatewayContainer implements LifeCycle {

    private final FerrymanConfig ferrymanConfig;		//	核心配置类

    private NettyHttpServer nettyHttpServer;	//	接收http请求的server

    private NettyHttpClient nettyHttpClient;	//	http转发的核心类

    private NettyProcessor nettyProcessor;		//	核心处理器

    public GatewayContainer(FerrymanConfig ferrymanConfig) {
        this.ferrymanConfig = ferrymanConfig;
        init();
    }

    @Override
    public void init() {

        log.info("#GatewayContainer# init begin ");

        //	核心处理器
        nettyProcessor = new NettyCoreProcessor();

        //	创建 NettyHttpServer
        nettyHttpServer = new NettyHttpServer(ferrymanConfig, nettyProcessor);

        //	创建NettyHttpClient
        nettyHttpClient = new NettyHttpClient(ferrymanConfig, nettyHttpServer.getEventLoopGroupWork());

        log.info("#GatewayContainer# init end ");
    }

    @Override
    public void start() {
        log.info("#GatewayContainer# start ");
        nettyProcessor.start();
        nettyHttpServer.start();
        nettyHttpClient.start();
    }

    @Override
    public void shutdown() {
        log.info("#GatewayContainer# shutdown ");
        nettyProcessor.shutdown();
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }
}
