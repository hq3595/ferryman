package com.dahua.ferryman.core.netty.processor.filter;

import com.dahua.ferryman.core.context.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:58
 */
@Slf4j
public abstract class AbstractProcessorFilterFactory implements ProcessorFilterFactory{

    /*
     *	pre + route + post
     */
    public DefaultProcessorFilterChain defaultProcessorFilterChain = new DefaultProcessorFilterChain("defaultProcessorFilterChain");

    /*
     * 	error + post
     */
    public DefaultProcessorFilterChain errorProcessorFilterChain = new DefaultProcessorFilterChain("errorProcessorFilterChain");

    /*
     * 	根据过滤器类型获取filter集合
     */
    public Map<String /* processorFilterType */, Map<String, ProcessorFilter<Context>>> processorFilterTypeMap = new LinkedHashMap<>();

    /*
     * 	根据过滤器id获取对应的Filter
     */
    public Map<String /* filterId */, ProcessorFilter<Context>> processorFilterIdMap = new LinkedHashMap<>();

    @Override
    public void buildFilterChain(ProcessorFilterType filterType, List<ProcessorFilter<Context>> filters) throws Exception {
        switch (filterType) {
            case PRE:
            case ROUTE:
                addFilterForChain(defaultProcessorFilterChain, filters);
                break;
            case ERROR:
                addFilterForChain(errorProcessorFilterChain, filters);
                break;
            case POST:
                addFilterForChain(defaultProcessorFilterChain, filters);
                addFilterForChain(errorProcessorFilterChain, filters);
                break;
            default:
                throw new RuntimeException("ProcessorFilterType is not supported !");
        }

    }

    private void addFilterForChain(DefaultProcessorFilterChain processorFilterChain,
                                   List<ProcessorFilter<Context>> filters) throws Exception {
        for(ProcessorFilter<Context> processorFilter : filters) {
            processorFilter.init();
            doBuilder(processorFilterChain, processorFilter);
        }
    }

    private void doBuilder(DefaultProcessorFilterChain processorFilterChain,
                           ProcessorFilter<Context> processorFilter) {

        log.info("filterChain: {}, the scanner filter is : {}", processorFilterChain.getId(), processorFilter.getClass().getName());

        Filter annotation = processorFilter.getClass().getAnnotation(Filter.class);

        if(annotation != null) {
            //	构建过滤器链条，添加filter
            processorFilterChain.addLast((AbstractLinkedProcessorFilter<Context>)processorFilter);

            //	映射到过滤器集合
            String filterId = annotation.id();
            if(filterId == null || filterId.length() < 1) {
                filterId = processorFilter.getClass().getName();
            }
            String code = annotation.value().getCode();
            Map<String, ProcessorFilter<Context>> filterMap = processorFilterTypeMap.get(code);
            if(filterMap == null) {
                filterMap = new LinkedHashMap<String, ProcessorFilter<Context>>();
            }
            filterMap.put(filterId, processorFilter);

            //	type
            processorFilterTypeMap.put(code, filterMap);
            //	id
            processorFilterIdMap.put(filterId, processorFilter);
        }

    }

    public <T> T getFilter(Class<T> t) throws Exception {
        Filter annotation = t.getAnnotation(Filter.class);
        if(annotation != null) {
            String filterId = annotation.id();
            if(filterId == null || filterId.length() < 1) {
                filterId = t.getName();
            }
            return this.getFilter(filterId);
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public <T> T getFilter(String filterId) throws Exception {
        ProcessorFilter<Context> filter = null;
        if(!processorFilterIdMap.isEmpty()) {
            filter = processorFilterIdMap.get(filterId);
        }
        return (T)filter;
    }

}
