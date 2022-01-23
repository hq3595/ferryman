package com.dahua.ferryman.client;

import java.lang.annotation.*;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FerrymanInvoker {

    // 访问路径
    String path();

}
