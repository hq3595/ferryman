package com.dahua.ferryman.core.netty.processor.filter;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:36
 * 过滤器的类型定义
 */
public enum  ProcessorFilterType {

    PRE("PRE", "前置过滤器"),

    ROUTE("ROUTE", "中置过滤器"),

    ERROR("ERROR", "错误过滤器"),

    POST("POST", "后置过滤器");

    private final String code ;

    private final String message;

    ProcessorFilterType(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
