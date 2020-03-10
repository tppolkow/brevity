package com.fydp.backend.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

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
    public ProducerFactory<Long, String> producerFactory() {
//        Map<String, Object> configProps = new HashMap<>();
//        configProps.put(
//                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                bootstrapAddress);
//        configProps.put(
//                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                LongSerializer.class);
//        configProps.put(
//                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(new KafkaConfig(kafkaUrl, trustedCert, clientCert, clientKey, prefix).buildProducerDefaults());
    }

    @Bean
    public KafkaTemplate<Long, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
