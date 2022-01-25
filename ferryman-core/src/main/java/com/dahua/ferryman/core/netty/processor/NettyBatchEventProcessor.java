package com.dahua.ferryman.core.netty.processor;

import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.queue.flusher.ParallelFlusher;
import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.context.HttpRequestWrapper;
import com.dahua.ferryman.core.helper.ResponseHelper;
import com.lmax.disruptor.dsl.ProducerType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/25 下午8:05
 */
@Slf4j
public class NettyBatchEventProcessor implements NettyProcessor{

    private static final String THREAD_NAME_PREFIX = "ferryman-flusher-";

    private FerrymanConfig ferrymanConfig;

    private NettyCoreProcessor nettyCoreProcessor;

    private ParallelFlusher<HttpRequestWrapper> parallelFlusher;

    public NettyBatchEventProcessor(FerrymanConfig ferrymanConfig, NettyCoreProcessor nettyCoreProcessor) {
        this.ferrymanConfig = ferrymanConfig;
        this.nettyCoreProcessor = nettyCoreProcessor;
        ParallelFlusher.Builder<HttpRequestWrapper> builder = new ParallelFlusher.Builder<HttpRequestWrapper>()
                .setBufferSize(ferrymanConfig.getBufferSize())
                .setThreads(ferrymanConfig.getProcessThread())
                .setProducerType(ProducerType.MULTI)
                .setNamePrefix(THREAD_NAME_PREFIX)
                .setWaitStrategy(ferrymanConfig.getWaitStrategy());

        BatchEventProcessorListener batchEventProcessorListener = new BatchEventProcessorListener();
        builder.setEventListener(batchEventProcessorListener);
        this.parallelFlusher = builder.build();
    }

    @Override
    public void process(HttpRequestWrapper httpRequestWrapper) {
        this.parallelFlusher.add(httpRequestWrapper);
    }

    @Override
    public void start() {
        this.nettyCoreProcessor.start();
        this.parallelFlusher.start();
    }

    @Override
    public void shutdown() {
        this.nettyCoreProcessor.shutdown();
        this.parallelFlusher.shutdown();
    }

    public class BatchEventProcessorListener implements ParallelFlusher.EventListener<HttpRequestWrapper>{

        @Override
        public void onEvent(HttpRequestWrapper event) throws Exception {
            nettyCoreProcessor.process(event);
        }

        @Override
        public void onException(Throwable t, long sequence, HttpRequestWrapper event) {
            HttpRequest request = event.getFullHttpRequest();
            ChannelHandlerContext ctx = event.getCtx();
            try {
                log.error("#BatchEventProcessorListener# onException 请求处理失败, request: {}. errorMessage: {}",
                        request, t.getMessage(), t);

                //	首先构建响应对象
                FullHttpResponse fullHttpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
                //	判断是否保持连接
                if(!HttpUtil.isKeepAlive(request)) {
                    ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //	如果保持连接, 则需要设置一下响应头：key: CONNECTION,  value: KEEP_ALIVE
                    fullHttpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                    ctx.writeAndFlush(fullHttpResponse);
                }

            } catch (Exception e) {
                //	ignore
                log.error("#BatchEventProcessorListener# onException 请求回写失败, request: {}. errorMessage: {}",
                        request, e.getMessage(), e);
            }
        }
    }


}
