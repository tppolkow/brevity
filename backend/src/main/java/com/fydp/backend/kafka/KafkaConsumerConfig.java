package com.fydp.backend.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${brevity.kafka.url}")
    private String kafkaUrl;

    @Value(value = "${brevity.kafka.trustedCert}")
    private String trustedCert;

    @Value(value = "${brevity.kafka.clientCert}")
    private String clientCert;

    @Value(value = "${brevity.kafka.clientKey}")
    private String clientKey;

    @Value(value = "${brevity.kafka.prefix}")
    private String prefix;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(
//                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                bootstrapAddress);
//        props.put(
//                ConsumerConfig.GROUP_ID_CONFIG,
//                KafkaConfig.groupId);
//        props.put(
//                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class);
//        props.put(
//                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(new KafkaConfig(kafkaUrl, trustedCert, clientCert, clientKey, prefix).buildConsumerDefaults());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }
}