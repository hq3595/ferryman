package com.dahua.ferryman.core.netty;

import com.dahua.ferryman.common.utils.NetUtils;
import com.dahua.ferryman.common.utils.RemotingHelper;
import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.lifecycle.LifeCycle;
import com.dahua.ferryman.core.netty.processor.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

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
        this.serverBootstrap = new ServerBootstrap();
        if(useEPoll()){
            this.eventLoopGroupBoss = new EpollEventLoopGroup(ferrymanConfig.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("NettyBossEPoll"));
            this.eventLoopGroupWork = new EpollEventLoopGroup(ferrymanConfig.getEventLoopGroupWorkNum(),
                    new DefaultThreadFactory("NettyWorkEPoll"));
        }else{
            this.eventLoopGroupBoss = new NioEventLoopGroup(ferrymanConfig.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("NettyBossNio"));
            this.eventLoopGroupWork = new NioEventLoopGroup(ferrymanConfig.getEventLoopGroupWorkNum(),
                    new DefaultThreadFactory("NettyWorkNio"));
        }
    }

    @Override
    public void start() {
        ServerBootstrap handler = this.serverBootstrap
                .group(eventLoopGroupBoss,eventLoopGroupWork)
                .channel(useEPoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, 65535)
                .childOption(ChannelOption.SO_RCVBUF, 65535)
                .localAddress(new InetSocketAddress(this.port))
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                          new HttpServerCodec(),
                          new HttpObjectAggregator(ferrymanConfig.getMaxContentLength()),
                          new HttpServerExpectContinueHandler(),
                          new NettyServerConnectManagerHandler(),
                          new NettyHttpServerHandler(nettyProcessor)
                        );
                    }
                });
    }

    @Override
    public void shutdown() {
        if(eventLoopGroupBoss != null) {
            eventLoopGroupBoss.shutdownGracefully();
        }
        if(eventLoopGroupWork != null) {
            eventLoopGroupWork.shutdownGracefully();
        }
    }

    public boolean useEPoll() {
        return ferrymanConfig.isUseEPoll() && NetUtils.isLinuxPlatform() && Epoll.isAvailable();
    }

    //  获取NettyHttpServer的EventLoopGroupWork
    public EventLoopGroup getEventLoopGroupWork() {
        return eventLoopGroupWork;
    }

    static class NettyServerConnectManagerHandler extends ChannelDuplexHandler{
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            log.debug("NETTY SERVER PIPLINE: channelRegistered {}", remoteAddr);
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            log.debug("NETTY SERVER PIPLINE: channelUnregistered {}", remoteAddr);
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            log.debug("NETTY SERVER PIPLINE: channelActive {}", remoteAddr);
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            log.debug("NETTY SERVER PIPLINE: channelInactive {}", remoteAddr);
            super.channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent)evt;
                if(event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                    log.warn("NETTY SERVER PIPLINE: userEventTriggered: IDLE {}", remoteAddr);
                    ctx.channel().close();
                }
            }
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            final String remoteAddr = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
            log.warn("NETTY SERVER PIPLINE: remoteAddr： {}, exceptionCaught {}", remoteAddr, cause);
            ctx.channel().close();
        }
    }
}
