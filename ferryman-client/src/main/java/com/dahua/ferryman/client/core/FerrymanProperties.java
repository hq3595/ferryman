package com.dahua.ferryman.client.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:18
 */
@Data
@ConfigurationProperties(prefix = FerrymanProperties.FERRYMAN_PREFIX)
public class FerrymanProperties {

    public static final String FERRYMAN_PREFIX = "ferryman";

    /**
     * 	etcd注册中心地址
     */
    private String registryAddress;

    /**
     * 	etcd注册命名空间
     */
    private String namespace = FERRYMAN_PREFIX;

    /**
     * 	环境属性
     */
    private String env = "dev";

}
