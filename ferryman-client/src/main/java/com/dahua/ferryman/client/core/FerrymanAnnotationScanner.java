package com.dahua.ferryman.client.core;

import com.dahua.ferryman.client.FerrymanInvoker;
import com.dahua.ferryman.client.FerrymanService;
import com.dahua.ferryman.client.Protocol;
import com.dahua.ferryman.common.config.HttpServiceInvoker;
import com.dahua.ferryman.common.config.ServiceDefinition;
import com.dahua.ferryman.common.config.ServiceInvoker;
import com.dahua.ferryman.common.constants.BasicConst;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:22
 */
public class FerrymanAnnotationScanner {

    private FerrymanAnnotationScanner() {
    }

    private static class SingletonHolder {
        static final FerrymanAnnotationScanner INSTANCE = new FerrymanAnnotationScanner();
    }

    public static FerrymanAnnotationScanner getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 扫描传入的Bean对象，最终返回一个ServiceDefinition
     */
    public synchronized ServiceDefinition scanBuilder(Object bean, Object... args) {

        Class<?> clazz = bean.getClass();
        boolean isPresent = clazz.isAnnotationPresent(FerrymanService.class);

        if(isPresent) {
            FerrymanService ferrymanService = clazz.getAnnotation(FerrymanService.class);
            String serviceId = ferrymanService.serviceId();
            Protocol protocol = ferrymanService.protocol();
            String patternPath = ferrymanService.patternPath();
            String version = ferrymanService.version();

            ServiceDefinition serviceDefinition = new ServiceDefinition();
            Map<String /* invokerPath */, ServiceInvoker> invokerMap = new HashMap<String, ServiceInvoker>();

            Method[] methods = clazz.getMethods();
            if(methods != null && methods.length > 0) {
                for(Method method : methods) {
                    FerrymanInvoker ferrymanInvoker = method.getAnnotation(FerrymanInvoker.class);
                    if(ferrymanInvoker == null) {
                        continue;
                    }
                    String path = ferrymanInvoker.path();

                    switch (protocol) {
                        case HTTP:
                            HttpServiceInvoker httpServiceInvoker = createHttpServiceInvoker(path, bean, method);
                            invokerMap.put(path, httpServiceInvoker);
                            break;
                        default:
                            break;
                    }
                }
            }
            //	设置属性
            serviceDefinition.setUniqueId(serviceId + BasicConst.COLON_SEPARATOR + version);
            serviceDefinition.setServiceId(serviceId);
            serviceDefinition.setVersion(version);
            serviceDefinition.setProtocol(protocol.getCode());
            serviceDefinition.setPatternPath(patternPath);
            serviceDefinition.setEnable(true);
            serviceDefinition.setInvokerMap(invokerMap);
            return serviceDefinition;
        }

        return null;
    }

    /**
     * 构建HttpServiceInvoker对象
     */
    private HttpServiceInvoker createHttpServiceInvoker(String path, Object bean, Method method) {
        HttpServiceInvoker httpServiceInvoker = new HttpServiceInvoker();
        httpServiceInvoker.setInvokerPath(path);
        return httpServiceInvoker;
    }

}
