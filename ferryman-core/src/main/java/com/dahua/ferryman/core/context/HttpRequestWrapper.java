package com.dahua.ferryman.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:32
 */
@Data
public class HttpRequestWrapper {

    private FullHttpRequest fullHttpRequest;

    private ChannelHandlerContext ctx;

}
