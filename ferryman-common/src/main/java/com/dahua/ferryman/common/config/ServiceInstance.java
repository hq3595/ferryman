package com.dahua.ferryman.common.config;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午12:15
 * 服务实例：一个服务定义会对应多个服务实例
 */
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = -7559569289189228478L;

    /**
     * 	服务实例ID: ip:port
     */
    protected String serviceInstanceId;

    /**
     * 	服务定义唯一id： uniqueId
     */
    protected String uniqueId;

    /**
     * 	服务实例地址： ip:port
     */
    protected String address;

    /**
     * 	标签信息
     */
    protected String tags;

    /**
     * 	权重信息
     */
    protected Integer weight;

    /**
     * 	服务注册的时间戳：后面我们做负载均衡，warmup预热
     */
    protected long registerTime;

    /**
     * 	服务实例启用禁用
     */
    protected boolean enable = true;

    /**
     * 	服务实例对应的版本号
     */
    protected String version;

    public ServiceInstance() {
        super();
    }

    public ServiceInstance(String serviceInstanceId, String uniqueId, String address, String tags, Integer weight,
                           long registerTime, boolean enable, String version) {
        super();
        this.serviceInstanceId = serviceInstanceId;
        this.uniqueId = uniqueId;
        this.address = address;
        this.tags = tags;
        this.weight = weight;
        this.registerTime = registerTime;
        this.enable = enable;
        this.version = version;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(this == null || getClass() != o.getClass()) return false;
        ServiceInstance serviceInstance = (ServiceInstance)o;
        return Objects.equals(serviceInstanceId, serviceInstance.serviceInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceInstanceId);
    }

}
