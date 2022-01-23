package com.dahua.ferryman.core.netty.processor.filter;

import com.dahua.ferryman.core.context.Context;
import com.dahua.ferryman.core.helper.ResponseHelper;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午5:48
 * 抽象的带有链表形式的过滤器
 */
public abstract class AbstractLinkedProcessorFilter <T> implements ProcessorFilter<Context> {

    //	做一个链表里面的一个元素，必须要有下一个元素的引用
    protected AbstractLinkedProcessorFilter<T> next = null;

    @Override
    public void filterNext(Context ctx, Object... args) throws Throwable {

        //	上下文生命周期结束，直接返回
        if(ctx.isTerminated()) {
            return;
        }

        //  写回标记，可以继续往下处理（可能还有后置过滤器）
        if(ctx.isWrittened()) {
            ResponseHelper.writeResponse(ctx);
        }

        if(next != null) {
            if(!next.check(ctx)) {
                next.filterNext(ctx, args);
            } else {
                next.transformEntry(ctx, args);
            }
        } else {
            //	没有下一个节点了，已经到了链表的最后一个节点
            ctx.terminated();
        }

    }

    @Override
    public void transformEntry(Context ctx, Object... args) throws Throwable {
        //	子类调用：这里就是真正执行下一个节点(元素)的操作
        entry(ctx, args);
    }

    public void setNext(AbstractLinkedProcessorFilter<T> next) {
        this.next = next;
    }

    public AbstractLinkedProcessorFilter<T> getNext() {
        return next;
    }

}
