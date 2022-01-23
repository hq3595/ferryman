package com.dahua.ferryman.common.config;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午1:57
 */
public class AbstractServiceInvoker implements ServiceInvoker{

    protected String invokerPath;

    protected String ruleId;

    protected int timeout = 5000;

    @Override
    public String getInvokerPath() {
        return invokerPath;
    }

    @Override
    public void setInvokerPath(String invokerPath) {
        this.invokerPath = invokerPath;
    }

    @Override
    public String getRuleId() {
        return ruleId;
    }

    @Override
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
