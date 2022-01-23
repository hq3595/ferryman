package com.dahua.ferryman.discovery.etcd.api;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:30
 */
public interface WatcherListener {

    void watcherKeyChanged(EtcdClient etcdClient, EtcdChangedEvent event) throws Exception;

}
