package com.dahua.ferryman.core.netty.processor.filter;

import com.dahua.ferryman.core.context.Context;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午6:00
 */
public class DefaultProcessorFilterChain extends ProcessorFilterChain<Context> {

    private final String id;

    public DefaultProcessorFilterChain(String id) {
        this.id = id;
    }

    /**
     * 	虚拟头结点：dummyHead
     */
    AbstractLinkedProcessorFilter<Context> first = new AbstractLinkedProcessorFilter<Context>() {

        @Override
        public void entry(Context ctx, Object... args) throws Throwable {
            super.filterNext(ctx, args);
        }

        @Override
        public boolean check(Context ctx) throws Throwable {
            return true;
        }
    };

    /**
     * 	尾节点
     */
    AbstractLinkedProcessorFilter<Context> end = first;

    @Override
    public void addFirst(AbstractLinkedProcessorFilter<Context> filter) {
        filter.setNext(first.getNext());
        first.setNext(filter);
        if(end == first) {
            end = filter;
        }
    }

    @Override
    public void addLast(AbstractLinkedProcessorFilter<Context> filter) {
        end.setNext(filter);
        end = filter;
    }

    @Override
    public void setNext(AbstractLinkedProcessorFilter<Context> filter) {
        addLast(filter);
    }

    @Override
    public AbstractLinkedProcessorFilter<Context> getNext() {
        return first.getNext();
    }


    @Override
    public boolean check(Context ctx) throws Throwable {
        return true;
    }

    @Override
    public void entry(Context ctx, Object... args) throws Throwable {
        first.transformEntry(ctx, args);
    }

    public String getId() {
        return id;
    }

}
