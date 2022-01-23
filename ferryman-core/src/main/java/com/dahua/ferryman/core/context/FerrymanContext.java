package com.dahua.ferryman.core.context;

import com.dahua.ferryman.common.config.Rule;
import com.dahua.ferryman.common.utils.AssertUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午12:06
 * 网关请求上下文核心对象
 */
public class FerrymanContext extends BasicContext{

    private final FerrymanRequest ferrymanRequest;

    private final Rule rule;

    private FerrymanResponse ferrymanResponse;

    private FerrymanContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive,
                            FerrymanRequest ferrymanRequest, Rule rule) {
        super(protocol, nettyCtx, keepAlive);
        this.ferrymanRequest = ferrymanRequest;
        this.rule = rule;
    }

    /**
     * 建造者类
     */
    public static class Builder {

        private String protocol;

        private ChannelHandlerContext nettyCtx;

        private FerrymanRequest ferrymanRequest;

        private Rule rule;

        private boolean keepAlive;

        public Builder() {
        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setNettyCtx(ChannelHandlerContext nettyCtx) {
            this.nettyCtx = nettyCtx;
            return this;
        }

        public Builder setFerrymanRequest(FerrymanRequest ferrymanRequest) {
            this.ferrymanRequest = ferrymanRequest;
            return this;
        }

        public Builder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public FerrymanContext build() {
            AssertUtil.notNull(protocol, "protocol不能为空");
            AssertUtil.notNull(nettyCtx, "nettyCtx不能为空");
            AssertUtil.notNull(ferrymanRequest, "request不能为空");
            AssertUtil.notNull(rule, "rule不能为空");
            return new FerrymanContext(protocol, nettyCtx, keepAlive, ferrymanRequest, rule);
        }
    }

    /**
     * 获取必要的上下文参数，如果没有则抛出IllegalArgumentException
     */
    public <T> T getRequiredAttribute(AttributeKey<T> key) {
        T value = getAttribute(key);
        AssertUtil.notNull(value, "required attribute '" + key + "' is missing !");
        return value;
    }

    /**
     * 获取指定key的上下文参数，如果没有则返回第二个参数的默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttributeOrDefault(AttributeKey<T> key, T defaultValue) {
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    /**
     * 根据过滤器id获取对应的过滤器配置信息
     */
    public Rule.FilterConfig getFilterConfig(String filterId) {
        return rule.getFilterConfig(filterId);
    }

    /**
     * 获取上下文中唯一的UniqueId
     */
    public String getUniqueId() {
        return ferrymanRequest.getUniqueId();
    }

    /**
     * 重写覆盖父类：basicContext的该方法，主要用于真正的释放操作
     */
    public void releaseRequest() {
        if(requestReleased.compareAndSet(false, true)) {
            ReferenceCountUtil.release(ferrymanRequest.getFullHttpRequest());
        }
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    @Override
    public FerrymanRequest getRequest() {
        return ferrymanRequest;
    }

    /**
     * 调用该方法就是获取原始请求内容，不去做任何修改动作
     */
    public FerrymanRequest getOriginRequest() {
        return ferrymanRequest;
    }

    /**
     * 调用该方法区分于原始的请求对象操作，主要就是做属性修改的
     */
    public FerrymanRequest getRequestMutale() {
        return ferrymanRequest;
    }

    @Override
    public FerrymanResponse getResponse() {
        return ferrymanResponse;
    }

    @Override
    public void setResponse(Object response) {
        this.ferrymanResponse = (FerrymanResponse)response;
    }

}
