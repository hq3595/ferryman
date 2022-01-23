package com.dahua.ferryman.common.constants;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:26
 */
public enum  LoadBalanceStrategy {

    RANDOM("RANDOM","随机负载均衡策略"),
    ROUND_ROBIN("ROUND_ROBIN","轮询负载均衡策略");

    private String val;
    private String desc;

    LoadBalanceStrategy(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public String getVal() {
        return val;
    }

    public String getDesc() {
        return desc;
    }

}
