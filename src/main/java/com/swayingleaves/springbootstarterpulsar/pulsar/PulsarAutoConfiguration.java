package com.swayingleaves.springbootstarterpulsar.pulsar;

import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author zhenglin
 * @apiNote pulsar配置类
 */
@Configuration
@EnableConfigurationProperties(PulsarProperties.class)
public class PulsarAutoConfiguration {

    @Autowired
    PulsarProperties pulsarProperties;
    /**
     * 注册PulsarClient
     */
    @Bean
    @ConditionalOnMissingBean
    public PulsarClient pulsarClient() throws PulsarClientException {
        if (StringUtils.isNotBlank(pulsarProperties.getServiceUrl())) {
            return PulsarClient.builder()
                    .serviceUrl(pulsarProperties.getServiceUrl())
                    .ioThreads(pulsarProperties.getIoThreads())
                    .listenerThreads(pulsarProperties.getListenerThreads())
                    .build();
        }
        return null;
    }
}
