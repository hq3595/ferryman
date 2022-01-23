package com.dahua.ferryman.core.netty.processor.filter;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:42
 * 执行过滤器的接口操作
 */
public interface ProcessorFilter<T> {

    /**
     * 过滤器是否需要执行的校验方法
     */
    boolean check(T t) throws Throwable;

    /**
     * 真正执行过滤器的方法
     */
    void entry(T t,Object... args) throws Throwable;

    /**
     * 触发下一个过滤器执行
     */
    void filterNext(T t,Object... args) throws Throwable;

    /**
     * 对象传输的方法
     */
    void transformEntry(T t,Object... args) throws Throwable;

    /**
     * 过滤器初始化的方法，如果子类有需求则进行覆盖
     */
    default void init() throws Exception {

    }
}
