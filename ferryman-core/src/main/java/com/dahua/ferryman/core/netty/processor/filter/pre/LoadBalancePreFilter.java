package com.dahua.ferryman.core.netty.processor.filter.pre;

import com.dahua.ferryman.common.config.DynamicConfigManager;
import com.dahua.ferryman.common.config.ServiceInstance;
import com.dahua.ferryman.common.constants.LoadBalanceStrategy;
import com.dahua.ferryman.common.constants.ProcessorFilterConstants;
import com.dahua.ferryman.common.constants.Protocol;
import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.exception.BaseException;
import com.dahua.ferryman.core.balance.LoadBalance;
import com.dahua.ferryman.core.balance.LoadBalanceFactory;
import com.dahua.ferryman.core.context.AttributeKey;
import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.context.FerrymanContext;
import com.dahua.ferryman.core.context.FerrymanRequest;
import com.dahua.ferryman.core.netty.processor.filter.AbstractEntryProcessorFilter;
import com.dahua.ferryman.core.netty.processor.filter.Filter;
import com.dahua.ferryman.core.netty.processor.filter.FilterConfig;
import com.dahua.ferryman.core.netty.processor.filter.ProcessorFilterType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午7:50
 */
@Filter(
        id = ProcessorFilterConstants.LOADBALANCE_PRE_FILTER_ID,
        name = ProcessorFilterConstants.LOADBALANCE_PRE_FILTER_NAME,
        value = ProcessorFilterType.PRE,
        order = ProcessorFilterConstants.LOADBALANCE_PRE_FILTER_ORDER
)
public class LoadBalancePreFilter extends AbstractEntryProcessorFilter<LoadBalancePreFilter.Config> {

    public LoadBalancePreFilter() {
        super(LoadBalancePreFilter.Config.class);
    }

    @Override
    public void entry(Context ctx, Object... args) throws Throwable {
        try {
            FerrymanContext ferrymanContext = (FerrymanContext)ctx;
            LoadBalancePreFilter.Config config = (LoadBalancePreFilter.Config)args[0];
            LoadBalanceStrategy loadBalanceStrategy = config.getBalanceStrategy();
            String protocol = ferrymanContext.getProtocol();
            switch (protocol) {
                case Protocol.HTTP:
                    doHttpLoadBalance(ferrymanContext, loadBalanceStrategy);
                    break;
                default:
                    break;
            }
        } finally {
            super.filterNext(ctx, args);;
        }
    }

    private void doHttpLoadBalance(FerrymanContext ferrymanContext, LoadBalanceStrategy loadBalanceStrategy) {
        FerrymanRequest ferrymanRequest = ferrymanContext.getRequest();
        String uniqueId = ferrymanRequest.getUniqueId();
        Set<ServiceInstance> serviceInstances = DynamicConfigManager.getInstance()
                .getServiceInstanceByUniqueId(uniqueId);

        ferrymanContext.putAttribute(AttributeKey.MATCH_INSTANCES, serviceInstances);

        //	通过负载均衡枚举值获取负载均衡实例对象
        LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(loadBalanceStrategy);
        //	调用负载均衡实现，选择一个实例进行返回
        ServiceInstance serviceInstance = loadBalance.select(ferrymanContext);

        if(serviceInstance == null) {
            //	如果服务实例没有找到：终止请求继续执行，显示抛出异常
            ferrymanContext.terminated();
            throw new BaseException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }

        //	这一步非常关键：设置可修改的服务host，为当前选择的实例对象的address
        ferrymanContext.getRequestMutale().setModifyHost(serviceInstance.getAddress());
    }

    /**
     * 负载均衡前置过滤器配置
     */
    @Getter
    @Setter
    public static class Config extends FilterConfig {

        private LoadBalanceStrategy balanceStrategy = LoadBalanceStrategy.ROUND_ROBIN;

    }

}
