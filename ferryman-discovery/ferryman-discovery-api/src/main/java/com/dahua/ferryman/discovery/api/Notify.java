package com.dahua.ferryman.discovery.api;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:06
 */
public interface Notify {

    void put(String key, String value) throws Exception;

    void delete(String key) throws Exception;

}
