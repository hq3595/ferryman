package com.dahua.ferryman.core.netty.processor;

import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.exception.BaseException;
import com.dahua.ferryman.core.context.FerrymanContext;
import com.dahua.ferryman.core.context.HttpRequestWrapper;
import com.dahua.ferryman.core.helper.RequestHelper;
import com.dahua.ferryman.core.helper.ResponseHelper;
import com.dahua.ferryman.core.netty.processor.filter.DefaultProcessorFilterFactory;
import com.dahua.ferryman.core.netty.processor.filter.ProcessorFilterFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:34
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor{

    private ProcessorFilterFactory processorFilterFactory = DefaultProcessorFilterFactory.getInstance();

    @Override
    public void process(HttpRequestWrapper event) throws Exception {
        FullHttpRequest request = event.getFullHttpRequest();
        ChannelHandlerContext ctx = event.getCtx();

        try {
            //	1. 解析FullHttpRequest, 把他转换为我们自己想要的内部对象：Context
            FerrymanContext ferrymanContext = RequestHelper.doContext(request, ctx);

            //	2. 执行整个的过滤器逻辑：FilterChain
            processorFilterFactory.doFilterChain(ferrymanContext);

        } catch (BaseException e) {
            log.error("#NettyCoreProcessor# process 快速失败： code: {}, msg: {}",
                    e.getCode().getCode(), e.getCode().getMessage(), e);
            FullHttpResponse response = ResponseHelper.getHttpResponse(e.getCode());
            //	释放资源写回响应
            doWriteAndRelease(ctx, request, response);
        } catch (Throwable t) {
            log.error("#NettyCoreProcessor# process 网关内部未知错误异常", t);
            FullHttpResponse response = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            //	释放资源写回响应
            doWriteAndRelease(ctx, request, response);
        }
    }

    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        boolean release = ReferenceCountUtil.release(request);
        if(!release) {
            log.warn("#NettyCoreProcessor# doWriteAndRelease release fail 释放资源失败， request:{}, release:{}",
                    request.uri(),
                    false);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
