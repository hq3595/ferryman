package com.dahua.ferryman.core.config;

import com.dahua.ferryman.common.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/22 下午10:00
 * 网关配置信息加载类
 * 优先级：运行参数(最高) ->  jvm参数  -> 环境变量  -> 配置文件  -> 内部FerrymanConfig的默认属性值;
 */
@Slf4j
public class ConfigLoader {

    private final static String CONFIG_ENV_PREFIEX = "ferryman_";

    private final static String CONFIG_JVM_PREFIEX = "ferryman.";

    private final static String CONFIG_FILE = "ferryman.properties";

    private FerrymanConfig ferrymanConfig = new FerrymanConfig();

    private ConfigLoader(){}

    private static class Holder{
        private static final ConfigLoader INSTANCE = new ConfigLoader();
    }

    public static ConfigLoader getInstance(){
        return Holder.INSTANCE;
    }

    public FerrymanConfig getFerrymanConfig(){
        return ferrymanConfig;
    }

    public FerrymanConfig loadConfig(String[] args){

        log.info("#ConfigLoader# load config begin");

        //  1. 加载文件
        {
            InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            if(is != null){
                Properties properties = new Properties();
                try{
                    properties.load(is);
                    PropertiesUtils.properties2Object(properties,ferrymanConfig);
                }catch (IOException e){
                    log.warn("#ConfigLoader# load config file: {} is error", CONFIG_FILE, e);
                }finally {
                    try{
                        is.close();
                    }catch (Exception e){
                        //  ignore
                    }
                }
            }
        }

        //  2. 加载环境变量
        {

            Map<String,String> env = System.getenv();
            Properties properties = new Properties();
            properties.putAll(env);
            PropertiesUtils.properties2Object(properties,ferrymanConfig,CONFIG_ENV_PREFIEX);
        }

        //  3. 加载JVM参数
        {
            Properties properties = System.getProperties();
            PropertiesUtils.properties2Object(properties, ferrymanConfig, CONFIG_JVM_PREFIEX);
        }

        //  4. 加载运行参数
        if(args != null && args.length > 0){
            Properties properties = new Properties();
            for (String arg : args){
                if(arg.startsWith("--") && arg.contains("=")) {
                    properties.put(arg.substring(2, arg.indexOf("=")),arg.substring(arg.indexOf("=") + 1));
                }
            }
            PropertiesUtils.properties2Object(properties, ferrymanConfig);
        }

        log.info("#ConfigLoader# load config end");

        return ferrymanConfig;
    }

}
