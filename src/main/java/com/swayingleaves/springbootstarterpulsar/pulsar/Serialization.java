package com.swayingleaves.springbootstarterpulsar.pulsar;

/**
 * @author zhenglin
 * @since 2020/8/23 1:43 下午
 * @apiNote pulsar 消息类型定义
 */
public enum Serialization {
    /**
     * 字符串
     */
    STRING,
    /**
     * json数据
     */
    JSON,
    /**
     * json数组
     */
    JSON_ARRAY

    ;
}
