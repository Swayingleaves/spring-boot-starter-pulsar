package com.swayingleaves.springbootstarterpulsar.pulsar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenglin
 * @since 2020/8/22 7:11 下午
 * @apiNote pulsar template
 */
@Slf4j
public class PulsarTemplate {

    private Map<String, Producer<Object>> producerJsonMap = new ConcurrentHashMap<>();
    private Map<String, Producer<String>> producerStrMap = new ConcurrentHashMap<>();

    public PulsarTemplate(Map<String, Producer<Object>> producerJsonMap, Map<String, Producer<String>> producerStrMap) {
        this.producerJsonMap = producerJsonMap;
        this.producerStrMap = producerStrMap;
    }

    public MessageId sendJsonMsg(String topic, Object object) throws PulsarClientException {
        final JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(object));
        final Producer<Object> objectProducer = getObjectProducer(topic);
        return objectProducer.send(jsonObject);
    }

    public MessageId sendJsonArrayMsg(String topic, Object object) throws PulsarClientException {
        final JSONArray objects = JSONArray.parseArray(JSONArray.toJSONString(object));
        final Producer<Object> objectProducer = getObjectProducer(topic);
        return objectProducer.send(objects);
    }

    public MessageId sendStringMsg(String topic, String object) throws PulsarClientException {
        final Producer<String> stringProducer = getStringProducer(topic);
        return stringProducer.send(object);
    }

    private Producer<Object> getObjectProducer(String topic) {
        final Producer<Object> objectProducer = producerJsonMap.get(topic);
        if (objectProducer == null) {
            throw new RuntimeException("this producer topic:" + topic + " has not initialized");
        }
        return objectProducer;
    }

    private Producer<String> getStringProducer(String topic) {
        final Producer<String> objectProducer = producerStrMap.get(topic);
        if (objectProducer == null) {
            throw new RuntimeException("this producer topic:" + topic + " has not initialized");
        }
        return objectProducer;
    }
}
