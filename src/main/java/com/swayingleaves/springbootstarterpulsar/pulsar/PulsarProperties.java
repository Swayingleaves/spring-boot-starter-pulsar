package com.swayingleaves.springbootstarterpulsar.pulsar;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author zhenglin
 * @since 2020/8/22 4:19 下午
 * @apiNote pulsar配置类
 */
@ConfigurationProperties(prefix = "pulsar")
@Data
public class PulsarProperties {
    private String serviceUrl;
    private Integer ioThreads = 10;
    private Integer listenerThreads = 10;
    private List<String> jsonProducers;
    private List<String> stringProducers;
    private List<String> consumers;
}