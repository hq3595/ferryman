package com.dahua.ferryman.discovery.etcd.api;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:32
 */
public class EtcdClientNotInitException extends RuntimeException{

    private static final long serialVersionUID = -617743243793838282L;

    public EtcdClientNotInitException() {
        super();
    }

    public EtcdClientNotInitException(String message) {
        super(message);
    }

}
