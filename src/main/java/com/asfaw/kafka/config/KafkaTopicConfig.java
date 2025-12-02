package com.asfaw.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name("order")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailNotificationTopic() {
        return TopicBuilder.name("notification.email.trucksload")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic smsNotificationTopic() {
        return TopicBuilder.name("notification.sms.trucksload")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pushNotificationTopic() {
        return TopicBuilder.name("notification.push.trucksload")
                .partitions(1)
                .replicas(1)
                .build();
    }


}



