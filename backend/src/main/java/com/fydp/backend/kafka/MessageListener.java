package com.fydp.backend.kafka;

import com.fydp.backend.service.SummaryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private SummaryService summaryService;

    @KafkaListener(topics = "brevity_responses", groupId = KafkaConsumerConfig.groupId)
    public void listen(ConsumerRecord<Long, String> record){

        logger.info("Received message : \n"  + record.key() + " : " + record.value());
        Long summaryId = record.key();
        String summaryData = record.value();

        summaryService.finishSummary(summaryId, summaryData);
    }
}
