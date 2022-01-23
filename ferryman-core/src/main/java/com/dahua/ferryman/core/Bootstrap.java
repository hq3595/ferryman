package com.dahua.ferryman.core;

import com.dahua.ferryman.core.config.ConfigLoader;
import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.container.GatewayContainer;
import com.dahua.ferryman.core.discovery.RegistryManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午9:46
 * 网关项目启动主入口
 */
@Slf4j
public class Bootstrap {

    public static void main(String[] args){
        //  1. 加载网关配置信息
        FerrymanConfig ferrymanConfig = ConfigLoader.getInstance().loadConfig(args);

        // TODO 2. 插件模块 (主要用于后置过滤器，不影响网关主流程,后续再开发)

        //	3. 初始化服务注册管理中心（服务注册管理器）, 监听动态配置的新增、修改、删除
        try {
            RegistryManager.getInstance().initialized(ferrymanConfig);
        } catch (Exception e) {
            log.error("RegistryManager is failed", e);
        }

        //  last: 启动容器
        GatewayContainer gatewayContainer = new GatewayContainer(ferrymanConfig);
        gatewayContainer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                gatewayContainer.shutdown();
            }
        }));
    }

}
