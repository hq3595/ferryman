package com.dahua.ferryman.core.netty.processor.filter;

import com.dahua.ferryman.common.context.Context;

import java.util.List;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:35
 */
public interface ProcessorFilterFactory {


    /**
     * 根据过滤器类型，添加一组过滤器，用于构建过滤器链
     */
    void buildFilterChain(ProcessorFilterType filterType, List<ProcessorFilter<Context>> filters) throws Exception;

    /**
     * 正常情况下执行过滤器链条
     */
    void doFilterChain(Context ctx) throws Exception;

    /**
     * 错误、异常情况下执行该过滤器链条
     */
    void doErrorFilterChain(Context ctx) throws Exception;

    /**
     * 获取指定类类型的过滤器
     */
    <T> T getFilter(Class<T> t) throws Exception;

    /**
     * 获取指定ID的过滤器
     */
    <T> T getFilter(String filterId) throws Exception;
}
