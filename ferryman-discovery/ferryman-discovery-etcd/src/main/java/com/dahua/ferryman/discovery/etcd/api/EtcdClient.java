package com.dahua.ferryman.discovery.etcd.api;

import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.lease.LeaseRevokeResponse;
import io.etcd.jetcd.lease.LeaseTimeToLiveResponse;
import io.etcd.jetcd.lock.LockResponse;
import io.etcd.jetcd.lock.UnlockResponse;
import io.etcd.jetcd.support.CloseableClient;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午2:30
 */
public interface EtcdClient {

    String CHARSET = "utf-8";

    public void putKey(String key, String value) throws Exception;

    public CompletableFuture<PutResponse> putKeyCallFuture(String key, String value) throws Exception ;

    public KeyValue getKey(final String key) throws Exception;

    public void deleteKey(String key);

    public List<KeyValue> getKeyWithPrefix(String prefix);

    public void deleteKeyWithPrefix(String prefix);

    public long putKeyWithExpireTime(String key, String value, long expireTime);

    public long putKeyWithLeaseId(String key, String value, long leaseId) throws Exception;

    public long generatorLeaseId(long expireTime) throws Exception;

    public CloseableClient keepAliveSingleLease(long leaseId, StreamObserver<LeaseKeepAliveResponse> observer);

    public LeaseKeepAliveResponse keepAliveOnce(long leaseId) throws InterruptedException, ExecutionException;

    public LeaseKeepAliveResponse keepAliveOnce(long leaseId, long timeout) throws InterruptedException, ExecutionException, TimeoutException;

    public LeaseTimeToLiveResponse timeToLiveLease(long leaseId) throws InterruptedException, ExecutionException;

    public LeaseRevokeResponse revokeLease(long leaseId) throws InterruptedException, ExecutionException;

    public long getHeartBeatLeaseId() throws InterruptedException;

    public LockResponse lock(String lockName) throws Exception;

    public LockResponse lock(String lockName, long expireTime) throws Exception;

    public LockResponse lockByLeaseId(String lockName, long leaseId) throws Exception;

    public UnlockResponse unlock(String lockName) throws Exception;

    public void addWatcherListener(final String watcherKey, final boolean usePrefix, WatcherListener watcherListener);

    public void removeWatcherListener(final String watcherKey);

    public void addHeartBeatLeaseTimeoutNotifyListener(HeartBeatLeaseTimeoutListener heartBeatLeaseTimeoutListener);

    public void close();

}
