package com.nitrowise.kafkapoc;

import com.nitrowise.data.avro.OrderMessage;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderMessageListener {

    @KafkaListener(
            // containerFactory = "kafkaListenerContainerFactory",
            id = "order-user-printer",
            groupId = "order-user",
            topics = "RawOrderTopic",
            autoStartup = "${listen.auto.start:true}",
            concurrency = "${listen.concurrency:3}")
    public void listenAndPrintUser(OrderMessage order) {
        log.info("order for user: {}", order.getUserId());
    }

    @KafkaListener(
            // containerFactory = "kafkaListenerContainerFactory",
            id = "printing",
            groupId = "printing-group",
            topics = "RawOrderTopic",
            autoStartup = "${listen.auto.start:true}",
            concurrency = "${listen.concurrency:1}")
    public void printListener(OrderMessage order) {
        log.info("order message: {}", order);
        throw new RuntimeException("POISON MESSAGE");
    }
}
