package com.dahua.ferryman.core.netty.processor.filter;

import com.dahua.ferryman.common.config.Rule;
import com.dahua.ferryman.common.constants.BasicConst;
import com.dahua.ferryman.common.utils.JSONUtil;
import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.netty.processor.cache.DefaultCacheManager;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:50
 * 抽象的Filter 用于真正的Filter进行继承的
 */
@Slf4j
public abstract class AbstractEntryProcessorFilter <FilterConfigClass> extends AbstractLinkedProcessorFilter<Context> {

    protected Filter filterAnnotation;

    protected Cache<String, FilterConfigClass> cache;

    protected final Class<FilterConfigClass> filterConfigClass;

    public AbstractEntryProcessorFilter(Class<FilterConfigClass> filterConfigClass) {
        this.filterAnnotation = this.getClass().getAnnotation(Filter.class);
        this.filterConfigClass = filterConfigClass;
        this.cache = DefaultCacheManager.getInstance().create(DefaultCacheManager.FILTER_CONFIG_CACHE_ID);
    }

    @Override
    public boolean check(Context ctx) throws Throwable {
        return ctx.getRule().hashId(filterAnnotation.id());
    }

    @Override
    public void transformEntry(Context ctx, Object... args) throws Throwable {
        FilterConfigClass filterConfigClass = dynamicLoadCache(ctx, args);
        super.transformEntry(ctx, filterConfigClass);
    }

    /**
     * <B>方法名称：</B>dynamicLoadCache<BR>
     * <B>概要说明：</B>动态加载缓存：每一个过滤器的具体配置规则<BR>
     * @author JiFeng
     * @since 2021年12月16日 下午11:40:51
     * @param ctx
     * @param args
     */
    private FilterConfigClass dynamicLoadCache(Context ctx, Object[] args) {
        //	通过上下文对象拿到规则，再通过规则获取到指定filterId的FilterConfig
        Rule.FilterConfig filterConfig = ctx.getRule().getFilterConfig(filterAnnotation.id());

        //	定义一个cacheKey：
        String ruleId = ctx.getRule().getId();
        String cacheKey = ruleId + BasicConst.DOLLAR_SEPARATOR + filterAnnotation.id();

        FilterConfigClass fcc = cache.getIfPresent(cacheKey);
        if(fcc == null) {
            if(filterConfig != null && StringUtils.isNotEmpty(filterConfig.getConfig())) {
                String configStr = filterConfig.getConfig();
                try {
                    fcc = JSONUtil.parse(configStr, filterConfigClass);
                    cache.put(cacheKey, fcc);
                } catch (Exception e) {
                    log.error("#AbstractEntryProcessorFilter# dynamicLoadCache filterId: {}, config parse error: {}",
                            filterAnnotation.id(),
                            configStr,
                            e);
                }
            }
        }
        return fcc;
    }

}
