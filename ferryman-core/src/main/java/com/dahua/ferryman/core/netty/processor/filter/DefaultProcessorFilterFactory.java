package com.dahua.ferryman.core.netty.processor.filter;

import com.dahua.ferryman.core.context.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:04
 */
@Slf4j
public class DefaultProcessorFilterFactory extends AbstractProcessorFilterFactory {

    private static class SingletonHolder {
        private static final DefaultProcessorFilterFactory INSTANCE = new DefaultProcessorFilterFactory();
    }

    public static DefaultProcessorFilterFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //	构造方法：加载所有的ProcessorFilter子类的实现
    @SuppressWarnings("unchecked")
    private DefaultProcessorFilterFactory(){

        //	SPI方式加载filter的集合：
        Map<String , List<ProcessorFilter<Context>>> filterMap = new LinkedHashMap<>();

        //	通过ServiceLoader加载
        @SuppressWarnings("rawtypes")
        ServiceLoader<ProcessorFilter> serviceLoader = ServiceLoader.load(ProcessorFilter.class);

        for(ProcessorFilter<Context> filter : serviceLoader) {
            Filter annotation = filter.getClass().getAnnotation(Filter.class);
            if(annotation != null) {
                String filterType = annotation.value().getCode();
                List<ProcessorFilter<Context>> filterList = filterMap.get(filterType);
                if(filterList == null) {
                    filterList = new ArrayList<ProcessorFilter<Context>>();
                }
                filterList.add(filter);
                filterMap.put(filterType, filterList);
            }
        }

        //	java基础：枚举类循环也是有顺序的
        for(ProcessorFilterType filterType : ProcessorFilterType.values()) {
            List<ProcessorFilter<Context>> filterList = filterMap.get(filterType.getCode());
            if(filterList == null || filterList.isEmpty()) {
                continue;
            }

            Collections.sort(filterList, new Comparator<ProcessorFilter<Context>>() {
                @Override
                public int compare(ProcessorFilter<Context> o1, ProcessorFilter<Context> o2) {
                    return o1.getClass().getAnnotation(Filter.class).order() -
                            o2.getClass().getAnnotation(Filter.class).order();
                }
            });

            try {
                super.buildFilterChain(filterType, filterList);
            } catch (Exception e) {
                //	ignor
                log.error("#DefaultProcessorFilterFactory.buildFilterChain# 网关过滤器加载异常, 异常信息为：{}!",e.getMessage(), e);
            }
        }

    }

    /**
     * 正常过滤器链条执行：pre + route + post
     */
    @Override
    public void doFilterChain(Context ctx) throws Exception {
        try {
            defaultProcessorFilterChain.entry(ctx);
        } catch (Throwable e) {
            log.error("#DefaultProcessorFilterFactory.doFilterChain# ERROR MESSAGE: {}" , e.getMessage(), e);

            //	设置异常
            ctx.setThrowable(e);

            //	执行doFilterChain显示抛出异常时，Context上下文的生命周期为：Context.TERMINATED
            if(ctx.isTerminated()) {
                ctx.runned();
            }
            //	执行异常处理的过滤器链条
            doErrorFilterChain(ctx);
        }
    }

    /**
     * 异常过滤器链条执行：error + post
     */
    @Override
    public void doErrorFilterChain(Context ctx) throws Exception {
        try {
            errorProcessorFilterChain.entry(ctx);
        } catch (Throwable e) {
            log.error("#DefaultProcessorFilterFactory.doErrorFilterChain# ERROR MESSAGE: {}" , e.getMessage(), e);
        }
    }

}
