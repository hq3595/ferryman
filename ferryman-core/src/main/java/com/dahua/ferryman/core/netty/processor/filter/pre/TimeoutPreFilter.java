package com.dahua.ferryman.core.netty.processor.filter.pre;

import com.dahua.ferryman.common.constants.ProcessorFilterConstants;
import com.dahua.ferryman.common.constants.Protocol;
import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.context.FerrymanContext;
import com.dahua.ferryman.core.context.FerrymanRequest;
import com.dahua.ferryman.core.netty.processor.filter.AbstractEntryProcessorFilter;
import com.dahua.ferryman.core.netty.processor.filter.Filter;
import com.dahua.ferryman.core.netty.processor.filter.FilterConfig;
import com.dahua.ferryman.core.netty.processor.filter.ProcessorFilterType;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:19
 */
@Filter(
        id = ProcessorFilterConstants.TIMEOUT_PRE_FILTER_ID,
        name = ProcessorFilterConstants.TIMEOUT_PRE_FILTER_NAME,
        value = ProcessorFilterType.PRE,
        order = ProcessorFilterConstants.TIMEOUT_PRE_FILTER_ORDER
)
public class TimeoutPreFilter extends AbstractEntryProcessorFilter<TimeoutPreFilter.Config> {

    public TimeoutPreFilter() {
        super(TimeoutPreFilter.Config.class);
    }

    /**
     * 超时的过滤器核心方法实现
     */
    @Override
    public void entry(Context ctx, Object... args) throws Throwable {
        try {
            FerrymanContext ferrymanContext = (FerrymanContext)ctx;
            String protocol = ferrymanContext.getProtocol();
            TimeoutPreFilter.Config config = (TimeoutPreFilter.Config) args[0];
            switch (protocol) {
                case Protocol.HTTP:
                    FerrymanRequest ferrymanRequest = ferrymanContext.getRequest();
                    ferrymanRequest.setRequestTimeout(config.getTimeout());
                    break;
                default:
                    break;
            }
        } finally {
            //	非常重要的，一定要记得：驱动我们的过滤器链表
            super.filterNext(ctx, args);
        }
    }

    @Getter
    @Setter
    public static class Config extends FilterConfig {
        private Integer timeout;
    }

}
