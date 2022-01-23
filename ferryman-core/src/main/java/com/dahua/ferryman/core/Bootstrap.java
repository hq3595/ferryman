package com.dahua.ferryman.core;

import com.dahua.ferryman.core.config.ConfigLoader;
import com.dahua.ferryman.core.config.FerrymanConfig;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午9:46
 * 网关项目启动主入口
 */
public class Bootstrap {

    public static void main(String[] args){
        //  1. 加载网关配置信息
        FerrymanConfig ferrymanConfig = ConfigLoader.getInstance().loadConfig(args);


    }

}
