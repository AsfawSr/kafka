package com.asfaw.kafka.config;

import com.asfaw.kafka.notification.model.NotificationEventEnvelope;
import com.asfaw.kafka.order.model.OrderEventEnvelope;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, NotificationEventEnvelope> notificationEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NotificationEventEnvelopeCompatDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new NotificationEventEnvelopeCompatDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationEventEnvelope> notificationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationEventEnvelope> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notificationEventConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderEventEnvelope> orderEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderEventEnvelopeCompatDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new OrderEventEnvelopeCompatDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEventEnvelope> orderKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderEventEnvelope> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderEventConsumerFactory());
        return factory;
    }
}

