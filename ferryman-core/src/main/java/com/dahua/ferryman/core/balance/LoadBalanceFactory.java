package com.dahua.ferryman.core.balance;

import com.dahua.ferryman.common.constants.LoadBalanceStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:25
 */
public class LoadBalanceFactory {

    private final Map<LoadBalanceStrategy, LoadBalance> loadBalanceMap = new HashMap<>();
    private static final LoadBalanceFactory INSTANCE = new LoadBalanceFactory();

    private LoadBalanceFactory() {
        loadBalanceMap.put(LoadBalanceStrategy.RANDOM, new RandomLoadBalance());
        loadBalanceMap.put(LoadBalanceStrategy.ROUND_ROBIN, new RoundRobinLoadBalance());
    }

    public static LoadBalance getLoadBalance(LoadBalanceStrategy loadBalance) {
        return INSTANCE.loadBalanceMap.get(loadBalance);
    }

}
