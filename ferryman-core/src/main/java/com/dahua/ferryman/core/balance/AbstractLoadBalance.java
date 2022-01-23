package com.dahua.ferryman.core.balance;

import com.dahua.ferryman.common.config.ServiceInstance;
import com.dahua.ferryman.core.context.AttributeKey;
import com.dahua.ferryman.core.context.FerrymanContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:23
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public ServiceInstance select(FerrymanContext context) {

        //	MATCH_INSTANCES：服务实例列表现在还没有填充，需要LoadBalancePreFilter的时候进行获取并设置
        Set<ServiceInstance> matchInstance = context.getAttribute(AttributeKey.MATCH_INSTANCES);
        if(matchInstance == null || matchInstance.size() == 0) {
            return null;
        }

        List<ServiceInstance> instances = new ArrayList<ServiceInstance>(matchInstance);
        if(instances.size() == 1) {
            return instances.get(0);
        }

        ServiceInstance instance = doSelect(context, instances);
        context.putAttribute(AttributeKey.LOAD_INSTANCE, instance);
        return instance;
    }

    /**
     * 子类实现指定的负载均衡策略选择一个服务
     */
    protected abstract ServiceInstance doSelect(FerrymanContext context, List<ServiceInstance> instances);


    protected static int getWeight(ServiceInstance instance) {
        int weight = instance.getWeight() == null ? LoadBalance.DEFAULT_WEIGHT : instance.getWeight();
        if(weight > 0) {
            //	服务启动注册的时间
            long timestamp = instance.getRegisterTime();
            if(timestamp > 0L) {
                //	服务启动了多久：当前时间 - 注册时间
                int upTime = (int)(System.currentTimeMillis() - timestamp);
                //	默认预热时间 5min
                int warmup = LoadBalance.DEFAULT_WARMUP;
                if(upTime > 0 && upTime < warmup) {
                    weight = calculateWarmUpWeight(upTime, warmup, weight);
                }
            }
        }
        return weight;
    }

    /**
     * 计算服务在预热时间内的新权重
     */
    private static int calculateWarmUpWeight(int upTime, int warmUp, int weight) {
        int ww =(int)((float)upTime / ((float)warmUp / (float) weight));
        return ww < 1 ? 1 : (Math.min(ww, weight));
    }

}
