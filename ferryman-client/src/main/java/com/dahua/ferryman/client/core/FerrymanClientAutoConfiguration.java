package com.dahua.ferryman.client.core;

import com.dahua.ferryman.client.protocol.SpringMVCClientRegistryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 下午8:21
 */
@Configuration
@EnableConfigurationProperties(FerrymanProperties.class)
@ConditionalOnProperty(prefix = FerrymanProperties.FERRYMAN_PREFIX, name = {"registryAddress", "namespace"})
public class FerrymanClientAutoConfiguration {

    @Autowired
    private FerrymanProperties ferrymanProperties;

    @Bean
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringMVCClientRegistryManager.class)
    public SpringMVCClientRegistryManager springMVCClientRegistryManager() throws Exception {
        return new SpringMVCClientRegistryManager(ferrymanProperties);
    }

}
