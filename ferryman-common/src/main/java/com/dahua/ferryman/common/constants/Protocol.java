package com.dahua.ferryman.common.constants;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:18
 */
public interface Protocol {

    String HTTP = "http";

    static boolean isHttp(String protocol) {
        return HTTP.equals(protocol);
    }

}
