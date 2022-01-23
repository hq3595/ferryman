package com.dahua.ferryman.discovery.etcd.api;

import io.etcd.jetcd.KeyValue;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:31
 */
public class EtcdChangedEvent {

    public static enum Type {
        PUT,
        DELETE,
        UNRECOGNIZED;
    }

    private KeyValue prevKeyValue;

    private KeyValue curtkeyValue;

    private Type type;

    public EtcdChangedEvent(KeyValue prevKeyValue, KeyValue curtkeyValue, Type type) {
        this.prevKeyValue = prevKeyValue;
        this.curtkeyValue = curtkeyValue;
        this.type = type;
    }

    public KeyValue getCurtkeyValue() {
        return curtkeyValue;
    }

    public KeyValue getPrevKeyValue() {
        return prevKeyValue;
    }

    public Type getType() {
        return type;
    }

}
