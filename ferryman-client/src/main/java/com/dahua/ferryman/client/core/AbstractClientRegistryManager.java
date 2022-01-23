package com.dahua.ferryman.client.core;

import com.dahua.ferryman.common.config.ServiceDefinition;
import com.dahua.ferryman.common.config.ServiceInstance;
import com.dahua.ferryman.common.constants.BasicConst;
import com.dahua.ferryman.common.utils.FastJsonConvertUtil;
import com.dahua.ferryman.discovery.api.Registry;
import com.dahua.ferryman.discovery.api.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:14
 */
@Slf4j
public abstract class AbstractClientRegistryManager {

    public static final String PROPERTIES_PATH = "ferryman.properties";

    public static final String REGISTRY_ADDRESS_KEY = "registryAddress";

    public static final String NAMESPACE_KEY = "namespace";

    public static final String ENV_KEY = "env";

    protected volatile boolean whetherStart = false;

    public static Properties properties = new Properties();

    protected static String registryAddress ;

    protected static String namespace ;

    protected static String env ;

    protected static String superPath;

    protected static String servicesPath;

    protected static String instancesPath;

    protected static String rulesPath;

    private RegistryService registryService;

    //	静态代码块读取ferryman.properties配置文件
    static {
        InputStream is = null;
        is = AbstractClientRegistryManager.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
        try {
            if(is != null) {
                properties.load(is);
                registryAddress = properties.getProperty(REGISTRY_ADDRESS_KEY);
                namespace = properties.getProperty(NAMESPACE_KEY);
                env = properties.getProperty(ENV_KEY);
                if(StringUtils.isBlank(registryAddress)) {
                    String errorMessage = "网关注册配置地址不能为空";
                    log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
                if(StringUtils.isBlank(namespace)) {
                    namespace = FerrymanProperties.FERRYMAN_PREFIX;
                }
            }
        } catch (Exception e) {
            log.error("#AbstractClientRegisteryManager# InputStream load is error", e);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                    //	ignore
                    log.error("#AbstractClientRegisteryManager# InputStream close is error", ex);
                }
            }
        }
    }

    /**
     * application.properties/yml 优先级是最高的
     */
    protected AbstractClientRegistryManager(FerrymanProperties ferrymanProperties) throws Exception {
        //	1. 初始化加载配置信息
        if(ferrymanProperties.getRegistryAddress() != null) {
            registryAddress = ferrymanProperties.getRegistryAddress();
            namespace = ferrymanProperties.getNamespace();
            if(StringUtils.isBlank(namespace)) {
                namespace = FerrymanProperties.FERRYMAN_PREFIX;
            }
            env = ferrymanProperties.getEnv();
        }

        //	2. 初始化加载注册中心对象
        ServiceLoader<RegistryService> serviceLoader = ServiceLoader.load(RegistryService.class);
        for(RegistryService registryService : serviceLoader) {
            registryService.initialized(ferrymanProperties.getRegistryAddress());
            this.registryService = registryService;
        }

        //	3. 注册构建顶级目录结构
        generatorStructPath(Registry.PATH + namespace + BasicConst.BAR_SEPARATOR + env);
    }

    /**
     * 注册顶级结构目录路径，只需要构建一次即可
     */
    private void generatorStructPath(String path) throws Exception {
        superPath = path;
        registryService.registerPathIfNotExists(superPath, "", true);
        registryService.registerPathIfNotExists(servicesPath = superPath + Registry.SERVICE_PREFIX, "", true);
        registryService.registerPathIfNotExists(instancesPath = superPath + Registry.INSTANCE_PREFIX, "", true);
        registryService.registerPathIfNotExists(rulesPath = superPath + Registry.RULE_PREFIX, "", true);
    }

    /**
     * 注册服务定义 对象
     */
    protected void registerServiceDefinition(ServiceDefinition serviceDefinition) throws Exception {
        /**
         * 	/ferryman-env
         * 		/services
         * 			/serviceA:1.0.0  ==> ServiceDefinition
         * 			/serviceA:2.0.0
         * 			/serviceB:1.0.0
         * 		/instances
         * 			/serviceA:1.0.0/192.168.11.100:port	 ==> ServiceInstance
         * 			/serviceA:1.0.0/192.168.11.101:port
         * 			/serviceB:1.0.0/192.168.11.102:port
         * 			/serviceA:2.0.0/192.168.11.103:port
         * 		/rules
         * 			/ruleId1	==>	Rule
         * 			/ruleId2
         * 		/gateway
         */
        String key = servicesPath
                + Registry.PATH
                + serviceDefinition.getUniqueId();

        if(!registryService.isExistKey(key)) {
            String value = FastJsonConvertUtil.convertObjectToJSON(serviceDefinition);
            registryService.registerPathIfNotExists(key, value, true);
        }
    }

    /**
     * 注册服务实例方法
     */
    protected void registerServiceInstance(ServiceInstance serviceInstance) throws Exception {
        String key = instancesPath
                + Registry.PATH
                + serviceInstance.getUniqueId()
                + Registry.PATH
                + serviceInstance.getServiceInstanceId();
        if(!registryService.isExistKey(key)) {
            String value = FastJsonConvertUtil.convertObjectToJSON(serviceInstance);
            registryService.registerPathIfNotExists(key, value, false);
        }
    }

    public static String getRegistryAddress() {
        return registryAddress;
    }

    public static String getNamespace() {
        return namespace;
    }

    public static String getEnv() {
        return env;
    }

    public static String getSuperPath() {
        return superPath;
    }

    public static String getServicesPath() {
        return servicesPath;
    }

    public static String getInstancesPath() {
        return instancesPath;
    }

    public static String getRulesPath() {
        return rulesPath;
    }

}
