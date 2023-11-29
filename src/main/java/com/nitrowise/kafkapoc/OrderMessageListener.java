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
            // id = "myListener",
            topics = "RawOrderTopic",
            autoStartup = "${listen.auto.start:true}",
            concurrency = "${listen.concurrency:3}")
    public void listen(OrderMessage order) {
        log.info("order message: {}", order);
    }
}
