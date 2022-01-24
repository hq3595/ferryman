package com.dahua.ferryman.common.constants;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:09
 */
public interface ProcessorFilterConstants {

    String LOADBALANCE_PRE_FILTER_ID = "loadBalancePreFilter";
    String LOADBALANCE_PRE_FILTER_NAME = "负载均衡前置过滤器";
    int LOADBALANCE_PRE_FILTER_ORDER = 2000;

    String TIMEOUT_PRE_FILTER_ID = "timeoutPreFilter";
    String TIMEOUT_PRE_FILTER_NAME = "超时过滤器";
    int TIMEOUT_PRE_FILTER_ORDER = 2100;

    String HTTP_ROUTE_FILTER_ID = "httpRouteFilter";
    String HTTP_ROUTE_FILTER_NAME = "httpRouteFilter";
    int HTTP_ROUTE_FILTER_ORDER = 5000;

    String DEFAULT_ERROR_FILTER_ID = "defaultErrorFilter";
    String DEFAULT_ERROR_FILTER_NAME = "默认的异常处理过滤器";
    int DEFAULT_ERROR_FILTER_ORDER = 20000;

}
