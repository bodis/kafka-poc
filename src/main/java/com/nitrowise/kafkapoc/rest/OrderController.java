package com.nitrowise.kafkapoc.rest;

import com.nitrowise.data.avro.OrderMessage;
import com.nitrowise.kafkapoc.entity.UserEntity;
import com.nitrowise.kafkapoc.model.OrderDTO;
import com.nitrowise.kafkapoc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.engine.spi.IdentifierValue;
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

    @Autowired
    private UserRepository userRepository;

    private static Faker FAKER = new Faker();

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
        orderKafkaTemplate.send(orderKafkaTemplate.getDefaultTopic(), orderMessage.getId(), orderMessage);
        return order;
    }

    @PostMapping("/generate/{count}")
    public void generate(@PathVariable("count") int count) {
        log.info("generate orders > {}", count);
        List<Long> userIds = userRepository.findAll().stream().map(UserEntity::getId).toList();

        for (int i = 0; i < count; i++) {
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setId(FAKER.random().nextInt());
            orderMessage.setCount(FAKER.random().nextInt(10));
            orderMessage.setPrice(FAKER.random().nextInt(100, 99999));
            orderMessage.setUserId(FAKER.random().nextInt(0, userIds.size() - 1));
            orderMessage.setProductId(FAKER.random().nextInt(1, 100));
            orderKafkaTemplate.send(orderKafkaTemplate.getDefaultTopic(), orderMessage.getId(), orderMessage);
        }
    }

}
