package com.dahua.ferryman.core.container;

import com.dahua.ferryman.core.config.FerrymanConfig;
import com.dahua.ferryman.core.lifecycle.LifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:26
 */
@Slf4j
public class GatewayContainer implements LifeCycle {

    private final FerrymanConfig ferrymanConfig;		//	核心配置类

    public GatewayContainer(FerrymanConfig ferrymanConfig) {
        this.ferrymanConfig = ferrymanConfig;
        init();
    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }
}
