package com.dahua.ferryman.core.context;

import com.dahua.ferryman.common.config.ServiceInstance;
import com.dahua.ferryman.common.config.ServiceInvoker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午12:14
 */
public abstract class AttributeKey<T> {

    private static final Map<String, AttributeKey<?>> namedMap = new HashMap<>();

    public static final AttributeKey<ServiceInvoker> HTTP_INVOKER = create(ServiceInvoker.class);

    //	存储所有服务实例的列表信息，负载均衡使用
    public static final AttributeKey<Set<ServiceInstance>> MATCH_INSTANCES = create(Set.class);

    //	负载均衡选中的实例信息
    public static final AttributeKey<ServiceInstance> LOAD_INSTANCE = create(ServiceInstance.class);


    static {
        namedMap.put("HTTP_INVOKER", HTTP_INVOKER);
        namedMap.put("MATCH_INSTANCES", MATCH_INSTANCES);
        namedMap.put("LOAD_INSTANCE", LOAD_INSTANCE);
    }

    public static AttributeKey<?> valueOf(String name) {
        return namedMap.get(name);
    }

    /**
     * 给我一个对象，转成对应的class类型
     */
    public abstract T cast(Object value);

    /**
     * 对外暴露创建AttributeKey
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> AttributeKey<T> create(final Class<? super T> valueClass) {
        return new SimpleAttributeKey(valueClass);
    }

    /**
     * 简单的属性Key转换类
     */
    public static class SimpleAttributeKey<T> extends AttributeKey<T> {

        private final Class<T> valueClass;

        SimpleAttributeKey(final Class<T> valueClass) {
            this.valueClass = valueClass;
        }

        @Override
        public T cast(Object value) {
            return valueClass.cast(value);
        }

        @Override
        public String toString() {
            if(valueClass != null) {
                StringBuilder sb = new StringBuilder(getClass().getName());
                sb.append("<");
                sb.append(valueClass.getName());
                sb.append(">");
                return sb.toString();
            }
            return super.toString();
        }

    }

}
