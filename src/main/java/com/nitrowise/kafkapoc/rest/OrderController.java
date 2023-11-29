package com.nitrowise.kafkapoc.rest;

import com.nitrowise.data.avro.OrderMessage;
import com.nitrowise.kafkapoc.model.OrderDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    @Autowired
    private KafkaTemplate<Long, OrderMessage> orderKafkaTemplate;

    @GetMapping("/{orderId}")
    public OrderDTO getOrder(@PathVariable("orderId") long orderId) {
        log.debug("get order > id:{}", orderId);
        OrderDTO order = new OrderDTO();
        order.setId(orderId);
        return order;
    }

    @PostMapping()
    public OrderDTO addOrder(@RequestBody OrderDTO order) {
        log.info("new order > {}", order);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setCount(order.getCount());
        orderMessage.setPrice(order.getPrice());
        orderMessage.setUserId(order.getUserId());
        orderMessage.setProductId(order.getProductId());
        orderKafkaTemplate.sendDefault(orderMessage);
        return order;
    }



}
