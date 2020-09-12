package com.swayingleaves.springbootstarterpulsar.pulsar;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhenglin
 * @since 2020/8/22 4:19 下午
 * @apiNote 在bean加载前实例化所有带有@PulsarConsumer注解的pulsar-consumer
 */
@Configuration
@Slf4j
@DependsOn({"pulsarClient"})
public class PulsarListenerRegister implements BeanPostProcessor {

    @Autowired
    private PulsarClient pulsarClient;
    @Autowired
    private PulsarProperties pulsarProperties;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> aClass = bean.getClass();
        final Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            final PulsarConsumer annotation = declaredMethod.getAnnotation(PulsarConsumer.class);
            if (annotation != null) {
                if (StringUtils.isNotBlank(annotation.topic())) {
                    ConsumerHolder consumerHolder = new ConsumerHolder(annotation,declaredMethod,bean);
                    final Consumer<?> consumer = subscribe(consumerHolder);
                    final ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(new PulsarListener(consumerHolder,consumer));
                }else {
                    throw new IllegalArgumentException("the method "+aClass.getName() + "."+ declaredMethod.getName()+" use @PulsarConsumer annotation topic is blank");
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private Consumer<?> subscribe(ConsumerHolder consumerHolder){
        try {
            final PulsarConsumer annotation = consumerHolder.getAnnotation();
            final String topic = calculationExpression(annotation.topic());
            final SubscriptionType subscriptionType = annotation.subscriptionType();
            return pulsarClient
                    .newConsumer()
                    .subscriptionName("subscription-" + topic)
                    .topic(topic)
                    .subscriptionType(subscriptionType)
                    .subscribe();
        } catch (PulsarClientException e) {
            throw new RuntimeException("Failed to init consumer.", e);
        }
    }

    @Bean
    public PulsarTemplate pulsarTemplate() throws PulsarClientException {
        Map<String, Producer<Object>> producerJsonMap = new ConcurrentHashMap<>(16);
        Map<String, Producer<String>> producerStrMap = new ConcurrentHashMap<>(16);

        final List<String> jsonProducers = pulsarProperties.getJsonProducers();
        final List<String> stringProducers = pulsarProperties.getStringProducers();

        if (!CollectionUtils.isEmpty(jsonProducers)){
            for (String topic : jsonProducers) {
                final Producer<Object> producer = pulsarClient.newProducer(Schema.JSON(Object.class)).topic(topic).create();
                producerJsonMap.put(topic,producer);
            }
        }

        if (!CollectionUtils.isEmpty(stringProducers)){
            for (String topic : stringProducers) {
                final Producer<String> producer = pulsarClient.newProducer(Schema.STRING).topic(topic).create();
                producerStrMap.put(topic,producer);
            }
        }
        return new PulsarTemplate(producerJsonMap,producerStrMap);
    }

    /**
     * 计算spel表达式
     * @param str
     * @return
     */
    private String calculationExpression(String str){
        //如果是以#开头将作为spel来解析获取topic
        if (str.startsWith("#")){
            str = str.substring(1);
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext context = new StandardEvaluationContext(pulsarProperties);
            final String value = parser.parseExpression(str).getValue(context, String.class);
            if (StringUtils.isBlank(value)){
                throw new NullPointerException("this expression has no attribute in pulsarProperties");
            }
            return value;
        }else {
            return str;
        }
    }
}
