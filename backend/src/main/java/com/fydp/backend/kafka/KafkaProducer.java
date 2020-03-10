package com.fydp.backend.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Value(value = "${brevity.kafka.request.topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    public void sendMessage(String msg) {
        kafkaTemplate.send(topicName, msg);
    }

    public void sendMessageWithKey(String msg, Long key) {
        logger.info("Sending message to topic");
        kafkaTemplate.send(topicName, key, msg);
    }

    public KafkaProducer(){

    }

}
