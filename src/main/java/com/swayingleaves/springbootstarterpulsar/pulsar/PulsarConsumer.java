package com.swayingleaves.springbootstarterpulsar.pulsar;

import org.apache.pulsar.client.api.SubscriptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhenglin
 * @since 2020/8/22 4:12 下午
 * @apiNote  Puslar-consumer注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PulsarConsumer {
    /**
     * 话题
     * 1、支持类spEl的表达式，且必须为
     *   {@see PulsarProperties} 的consumers属性的值，
     *   且以 '#' 开头，数组/集合使用array[index]获取该index下的值,如: #consumerTopics[1];
     * 2、支持字符串topic，如：topic1 ;不以 '#'  开头;
     *    意为非PulsarProperties的consumers值将直接将传入的非空topic创建pulsar consumers;
     *
     */
    String topic();
    /**
     * 序列化
     */
    Serialization clazz() default Serialization.JSON;
    /**
     * 消费者的类型
     */
    SubscriptionType subscriptionType() default SubscriptionType.Shared;

}
