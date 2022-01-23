package com.dahua.ferryman.common.config;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午12:14
 */
public interface ServiceInvoker {

    /**
     * 获取真正的服务调用的全路径
     */
    String getInvokerPath();

    void setInvokerPath(String invokerPath);

    /**
     * 获取指定服务调用绑定的唯一规则
     */
    String getRuleId();

    void setRuleId(String ruleId);

    /**
     * 获取该服务调用
     */
    int getTimeout();

    void setTimeout(int timeout);

}
