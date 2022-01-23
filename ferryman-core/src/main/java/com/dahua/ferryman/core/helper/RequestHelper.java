package com.dahua.ferryman.core.helper;

import com.dahua.ferryman.common.config.DynamicConfigManager;
import com.dahua.ferryman.common.config.Rule;
import com.dahua.ferryman.common.config.ServiceDefinition;
import com.dahua.ferryman.common.config.ServiceInvoker;
import com.dahua.ferryman.common.constants.BasicConst;
import com.dahua.ferryman.common.constants.Protocol;
import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.exception.BaseException;
import com.dahua.ferryman.common.utils.AntPathMatcher;
import com.dahua.ferryman.core.context.AttributeKey;
import com.dahua.ferryman.core.context.FerrymanContext;
import com.dahua.ferryman.core.context.FerrymanRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:42
 * 解析请求信息，构建上下文对象
 */
public class RequestHelper {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    // 解析FullHttpRequest 构建FerrymanContext核心构建方法
    public static FerrymanContext doContext(FullHttpRequest request, ChannelHandlerContext ctx) {

        // FerrymanRequest
        FerrymanRequest ferrymanRequest = doRequest(request,ctx);

        // 根据请求对象里的uniqueId，获取资源服务信息(也就是服务定义信息)
        ServiceDefinition serviceDefinition = getServiceDefinition(ferrymanRequest);

        // 快速路径匹配失败的策略
        if(!ANT_PATH_MATCHER.match(serviceDefinition.getPatternPath(), ferrymanRequest.getPath())) {
            throw new BaseException(ResponseCode.PATH_NO_MATCHED);
        }

        // 根据请求对象获取服务定义对应的方法调用，然后获取对应的规则
        ServiceInvoker serviceInvoker = getServiceInvoker(ferrymanRequest, serviceDefinition);
        String ruleId = serviceInvoker.getRuleId();
        Rule rule = DynamicConfigManager.getInstance().getRule(ruleId);

        // 构建我们的FerrymanContext对象
        FerrymanContext ferrymanContext = new FerrymanContext.Builder()
                .setProtocol(serviceDefinition.getProtocol())
                .setFerrymanRequest(ferrymanRequest)
                .setNettyCtx(ctx)
                .setKeepAlive(HttpUtil.isKeepAlive(request))
                .setRule(rule)
                .build();

        // 设置一些必要的上下文参数用于后面使用
        putContext(ferrymanContext, serviceInvoker);

        return ferrymanContext;
    }

    //  构建 FerrymanRequest 请求对象
    private static FerrymanRequest doRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {

        HttpHeaders headers = fullHttpRequest.headers();
        //	从header头获取必须要传入的关键属性 uniqueId
        String uniqueId = headers.get(BasicConst.UNIQUE_ID);

        if(StringUtils.isBlank(uniqueId)) {
            throw new BaseException(ResponseCode.REQUEST_PARSE_ERROR_NO_UNIQUEID);
        }

        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String uri = fullHttpRequest.uri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null : HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);

        FerrymanRequest ferrymanRequest = new FerrymanRequest(uniqueId,
                charset,
                clientIp,
                host,
                uri,
                method,
                contentType,
                headers,
                fullHttpRequest);

        return ferrymanRequest;
    }

    // 根据请求对象和服务定义对象获取对应的ServiceInvoke
    private static ServiceInvoker getServiceInvoker(FerrymanRequest ferrymanRequest, ServiceDefinition serviceDefinition) {
        Map<String, ServiceInvoker> invokerMap = serviceDefinition.getInvokerMap();
        ServiceInvoker serviceInvoker = invokerMap.get(ferrymanRequest.getPath());
        if(serviceInvoker == null) {
            throw new BaseException(ResponseCode.SERVICE_INVOKER_NOT_FOUND);
        }
        return serviceInvoker;
    }

    // 通过请求对象获取服务资源信息
    private static ServiceDefinition getServiceDefinition(FerrymanRequest ferrymanRequest) {
        //	ServiceDefinition从哪里获取，就是在网关服务初始化的时候(加载的时候)？ 从缓存信息里获取
        ServiceDefinition serviceDefinition = DynamicConfigManager.getInstance().getServiceDefinition(ferrymanRequest.getUniqueId());
        //	做异常情况判断
        if(serviceDefinition == null) {
            throw new BaseException(ResponseCode.SERVICE_DEFINITION_NOT_FOUND);
        }
        return serviceDefinition;
    }

    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String xForwardedValue = request.headers().get(BasicConst.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if(StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if(values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if(clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }

    // 设置必要的上下文方法
    private static void putContext(FerrymanContext ferrymanContext, ServiceInvoker serviceInvoker) {
        switch (ferrymanContext.getProtocol()) {
            case Protocol.HTTP:
                ferrymanContext.putAttribute(AttributeKey.HTTP_INVOKER, serviceInvoker);
                break;
            default:
                break;
        }
    }
}
