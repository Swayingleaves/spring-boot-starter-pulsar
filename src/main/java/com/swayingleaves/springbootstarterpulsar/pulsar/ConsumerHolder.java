package com.swayingleaves.springbootstarterpulsar.pulsar;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author zhenglin
 * @since 2020/8/22 4:19 下午
 * @apiNote pulsar信息封装类
 */
@Data
public class ConsumerHolder {

    private final PulsarConsumer annotation;
    private final Method method;
    private final Object bean;

    ConsumerHolder(PulsarConsumer annotation, Method method, Object bean) {
        this.annotation = annotation;
        this.method = method;
        this.bean = bean;
    }

}
