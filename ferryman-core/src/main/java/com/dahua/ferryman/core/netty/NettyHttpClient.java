package com.dahua.ferryman.core.netty;

import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.helper.AsyncHttpHelper;
import com.dahua.ferryman.core.lifecycle.LifeCycle;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import java.io.IOException;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:28
 */
@Slf4j
public class NettyHttpClient implements LifeCycle {

    private FerrymanConfig ferrymanConfig;

    private EventLoopGroup eventLoopGroupWork;

    private AsyncHttpClient asyncHttpClient;

    private DefaultAsyncHttpClientConfig.Builder clientBuilder;

    public NettyHttpClient(FerrymanConfig ferrymanConfig, EventLoopGroup eventLoopGroupWork) {
        this.ferrymanConfig = ferrymanConfig;
        this.eventLoopGroupWork = eventLoopGroupWork;
        //	在构造函数调用初始化方法
        init();
    }

    @Override
    public void init() {
        this.clientBuilder = new DefaultAsyncHttpClientConfig.Builder()
                .setFollowRedirect(false)
                .setEventLoopGroup(eventLoopGroupWork)
                .setConnectTimeout(ferrymanConfig.getHttpConnectTimeout())
                .setRequestTimeout(ferrymanConfig.getHttpRequestTimeout())
                .setMaxRequestRetry(ferrymanConfig.getHttpMaxRequestRetry())
                .setAllocator(PooledByteBufAllocator.DEFAULT)
                .setCompressionEnforced(true)
                .setMaxConnections(ferrymanConfig.getHttpMaxConnections())
                .setMaxConnectionsPerHost(ferrymanConfig.getHttpConnectionsPerHost())
                .setPooledConnectionIdleTimeout(ferrymanConfig.getHttpPooledConnectionIdleTimeout());
    }

    @Override
    public void start() {
        this.asyncHttpClient = new DefaultAsyncHttpClient(clientBuilder.build());
        AsyncHttpHelper.getInstance().initialized(asyncHttpClient);
    }

    @Override
    public void shutdown() {
        if(asyncHttpClient != null) {
            try {
                this.asyncHttpClient.close();
            } catch (IOException e) {
                // ignore
                log.error("#NettyHttpClient.shutdown# shutdown error", e);
            }
        }
    }
}
