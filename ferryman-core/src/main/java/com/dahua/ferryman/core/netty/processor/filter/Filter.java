package com.dahua.ferryman.core.netty.processor.filter;

import java.lang.annotation.*;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:52
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Filter {

    // 过滤器的唯一ID
    String id();

    // 过滤器的名字
    String name() default "";

    // 过滤器的类型
    ProcessorFilterType value();

    // 按照此排序从小到大依次执行过滤器
    int order() default 0;

}
