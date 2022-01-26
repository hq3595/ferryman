package com.dahua.ferryman.core.netty.processor.filter.route;

import com.dahua.ferryman.common.constants.ProcessorFilterConstants;
import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.exception.BaseException;
import com.dahua.ferryman.common.exception.ConnectionException;
import com.dahua.ferryman.core.config.ConfigLoader;
import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.context.FerrymanContext;
import com.dahua.ferryman.core.context.FerrymanResponse;
import com.dahua.ferryman.core.helper.AsyncHttpHelper;
import com.dahua.ferryman.core.netty.processor.filter.AbstractEntryProcessorFilter;
import com.dahua.ferryman.core.netty.processor.filter.Filter;
import com.dahua.ferryman.core.netty.processor.filter.FilterConfig;
import com.dahua.ferryman.core.netty.processor.filter.ProcessorFilterType;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:10
 */
@Filter(
        id = ProcessorFilterConstants.HTTP_ROUTE_FILTER_ID,
        name = ProcessorFilterConstants.HTTP_ROUTE_FILTER_NAME,
        value = ProcessorFilterType.ROUTE,
        order = ProcessorFilterConstants.HTTP_ROUTE_FILTER_ORDER
)
@Slf4j
public class HttpRouteFilter extends AbstractEntryProcessorFilter<FilterConfig> {

    public HttpRouteFilter() {
        super(FilterConfig.class);
    }

    @Override
    public void entry(Context ctx, Object... args) throws Throwable {
        FerrymanContext ferrymanContext = (FerrymanContext)ctx;
        Request request = ferrymanContext.getRequestMutale().build();

        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        //	双异步和单异步模式
        boolean whenComplete = ConfigLoader.getInstance().getFerrymanConfig().isWhenComplete();

        //	单异步模式
        if(whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, ferrymanContext, args);
            });
        }
        //	双异步模式
        else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, ferrymanContext, args);
            });
        }
    }

    private void complete(Request request,
                          Response response,
                          Throwable throwable,
                          FerrymanContext ferrymanContext,
                          Object... args) {
        try {
            //	1. 释放请求资源
            ferrymanContext.releaseRequest();
            //	2. 判断是否有异常产生
            if(java.util.Objects.nonNull(throwable)) {
                String url = request.getUrl();
                //	超时异常
                if(throwable instanceof java.util.concurrent.TimeoutException) {
                    log.warn("#HttpRouteFilter# complete返回响应执行， 请求路径：{}，耗时超过 {}  ms.",
                            url,
                            (request.getRequestTimeout() == 0 ?
                                    ConfigLoader.getInstance().getFerrymanConfig().getHttpRequestTimeout() :
                                    request.getRequestTimeout())
                    );
                    //	网关里设置异常都是使用自定义异常
                    ferrymanContext.setThrowable(new BaseException(ResponseCode.REQUEST_TIMEOUT));
                }
                //	其他异常情况
                else {
                    ferrymanContext.setThrowable(new ConnectionException(throwable,
                            ferrymanContext.getUniqueId(),
                            url,
                            ResponseCode.HTTP_RESPONSE_ERROR));
                }
            }
            //	正常返回响应结果：
            else {
                //	设置响应信息
                ferrymanContext.setResponse(FerrymanResponse.buildResponse(response));
                //	正常返回，设置写回标记
            }

        } catch (Throwable t) {
            //	最终兜底异常处理
            ferrymanContext.setThrowable(new BaseException(ResponseCode.INTERNAL_ERROR));
            log.error("#HttpRouteFilter# complete catch到未知异常", t);
        } finally {
            try {
                // 	让异步线程内部自己进行触发下一个节点执行
                ferrymanContext.writtened();
                super.filterNext(ferrymanContext, args);
            } catch (Throwable t) {
                //	兜底处理，把异常信息放入上下文
                ferrymanContext.setThrowable(new BaseException(ResponseCode.INTERNAL_ERROR));
                log.error("#HttpRouteFilter# fireNext出现异常", t);
            }
        }
    }

}
