package com.dahua.ferryman.core.balance;

import com.dahua.ferryman.common.config.ServiceInstance;
import com.dahua.ferryman.core.context.FerrymanContext;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:22
 */
public interface LoadBalance {

    int DEFAULT_WEIGHT = 100;

    int DEFAULT_WARMUP = 5 * 60 * 1000;

    /**
     * 从所有实例列表中选择一个实例
     */
    ServiceInstance select(FerrymanContext context);

}
