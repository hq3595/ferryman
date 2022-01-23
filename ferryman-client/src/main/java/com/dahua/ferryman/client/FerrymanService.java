package com.dahua.ferryman.client;

import java.lang.annotation.*;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FerrymanService {

    // 服务的唯一ID
    String serviceId();

    // 服务版本号
    String version() default "1.0.0";

    // 协议类型
    Protocol protocol();

    // ANT路径匹配表达式配置
    String patternPath();

}
