package com.swayingleaves.springbootstarterpulsar.pulsar;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;

import java.lang.reflect.Method;

/**
 * @author zhenglin
 * @since 2020/8/22 5:21 下午
 * @apiNote pulsar线程
 */
@Slf4j
public class PulsarListener implements Runnable{

    private final ConsumerHolder consumerHolder;
    private final Consumer<?> consumer;

    PulsarListener(ConsumerHolder consumerHolder, Consumer<?> consumer){
        this.consumerHolder = consumerHolder;
        this.consumer = consumer;
    }

    @Override
    public void run() {
       try {
           final Method method = consumerHolder.getMethod();
           final Object bean = consumerHolder.getBean();
           final Serialization clazz = consumerHolder.getAnnotation().clazz();
           while (true){
               Message<?> msg = null;
               try {
                   msg = consumer.receive();
                   String msgStr = new String(msg.getData());
                   msgStr = msgStr.replaceAll("\\\\|\n"," ");
                   method.setAccessible(true);
                   switch (clazz){
                       case STRING: method.invoke(bean, msgStr);break;
                       case JSON_ARRAY: method.invoke(bean, JSON.parseArray(msgStr));break;
                       case JSON:
                       default: method.invoke(bean, JSON.parseObject(msgStr));break;
                   }
                   consumer.acknowledge(msg);
               } catch (Exception e) {
                   log.error(e.getMessage(),e);
                   consumer.negativeAcknowledge(msg);
               }
           }
       }catch (Exception e){
           log.error(e.getMessage(),e);
       }finally {
           try {
               consumer.close();
           } catch (PulsarClientException e) {
               log.error(e.getMessage(),e);
           }
       }
    }
}
