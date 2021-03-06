package com.dahua.ferryman.core.helper;

import com.dahua.ferryman.common.constants.BasicConst;
import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.exception.BaseException;
import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.context.FerrymanResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;

import java.util.Objects;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:35
 */
public class ResponseHelper {

    /**
     * 获取响应对象
     */
    public static FullHttpResponse getHttpResponse(ResponseCode responseCode) {
        FerrymanResponse ferrymanResponse = FerrymanResponse.buildResponse(responseCode);
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(ferrymanResponse.getContent().getBytes()));
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        return httpResponse;
    }

    /**
     * 通过上下文对象和FerrymanResponse对象 构建FullHttpResponse
     */
    private static FullHttpResponse getHttpResponse(Context ctx, FerrymanResponse ferrymanResponse) {

        // 有异常返回
        ByteBuf content;
        if(ctx.getThrowable() != null){
            if(ctx.getThrowable() instanceof BaseException){
                BaseException baseException = (BaseException)ctx.getThrowable();
                return getHttpResponse(baseException.getCode());
            }else{
                return getHttpResponse(ResponseCode.INTERNAL_ERROR);
            }
        }

        if(Objects.nonNull(ferrymanResponse.getFutureResponse())) {
            content = Unpooled.wrappedBuffer(ferrymanResponse.getFutureResponse()
                    .getResponseBodyAsByteBuffer());
        }
        else if(ferrymanResponse.getContent() != null) {
            content = Unpooled.wrappedBuffer(ferrymanResponse.getContent().getBytes());
        }
        else {
            content = Unpooled.wrappedBuffer(BasicConst.BLANK_SEPARATOR_1.getBytes());
        }

        if(Objects.isNull(ferrymanResponse.getFutureResponse())) {
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    ferrymanResponse.getHttpResponseStatus(),
                    content);
            httpResponse.headers().add(ferrymanResponse.getResponseHeaders());
            httpResponse.headers().add(ferrymanResponse.getExtraResponseHeaders());
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
            return httpResponse;
        } else {
            ferrymanResponse.getFutureResponse().getHeaders().add(ferrymanResponse.getExtraResponseHeaders());

            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(ferrymanResponse.getFutureResponse().getStatusCode()),
                    content);
            httpResponse.headers().add(ferrymanResponse.getFutureResponse().getHeaders());
            return httpResponse;
        }
    }


    /**
     * 写回响应信息方法
     */
    public static void writeResponse(Context ferrymanContext) {
        //	释放资源
        ferrymanContext.releaseRequest();

        if(ferrymanContext.isWrittened()) {
            //	1：第一步构建响应对象，并写回数据
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ferrymanContext, (FerrymanResponse) ferrymanContext.getResponse());
            //  短连接：直接关闭连接
            if(!ferrymanContext.isKeepAlive()) {
                ferrymanContext.getNettyCtx()
                        .writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
            }
            //	长连接：保持连接
            else {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                ferrymanContext.getNettyCtx()
                        .writeAndFlush(httpResponse);
//                        .addListener(ChannelFutureListener.CLOSE);
            }
            //	2:	设置写回结束状态为： COMPLETED
            ferrymanContext.completed();
        }
        else if(ferrymanContext.isCompleted()){
            ferrymanContext.invokeCompletedCallback();
        }
    }
}
