package com.dahua.ferryman.core.netty.processor.filter.error;

import com.dahua.ferryman.common.constants.ProcessorFilterConstants;
import com.dahua.ferryman.common.constants.ResponseCode;
import com.dahua.ferryman.common.exception.BaseException;
import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.context.FerrymanResponse;
import com.dahua.ferryman.core.netty.processor.filter.AbstractEntryProcessorFilter;
import com.dahua.ferryman.core.netty.processor.filter.Filter;
import com.dahua.ferryman.core.netty.processor.filter.FilterConfig;
import com.dahua.ferryman.core.netty.processor.filter.ProcessorFilterType;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午7:54
 */
@Filter(
        id = ProcessorFilterConstants.DEFAULT_ERROR_FILTER_ID,
        name = ProcessorFilterConstants.DEFAULT_ERROR_FILTER_NAME,
        value = ProcessorFilterType.ERROR,
        order = ProcessorFilterConstants.DEFAULT_ERROR_FILTER_ORDER
)
public class DefaultErrorFilter extends AbstractEntryProcessorFilter<FilterConfig> {

    public DefaultErrorFilter() {
        super(FilterConfig.class);
    }

    @Override
    public void entry(Context ctx, Object... args) throws Throwable {
        try {
            Throwable throwable = ctx.getThrowable();
            ResponseCode responseCode = ResponseCode.INTERNAL_ERROR;
            if(throwable instanceof BaseException) {
                BaseException baseException = (BaseException)throwable;
                responseCode = baseException.getCode();
            }
            FerrymanResponse ferrymanResponse = FerrymanResponse.buildResponse(responseCode);
            ctx.setResponse(ferrymanResponse);
        } finally {
            System.err.println("============> do error filter <===============");
            //	设置写回标记
            ctx.writtened();
            //	触发后面的过滤器执行
            super.filterNext(ctx, args);
        }
    }

}
