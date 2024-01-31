package com.nitrowise.kafkapoc.service;

import com.nitrowise.data.avro.UserMessage;
import com.nitrowise.kafkapoc.entity.UserEntity;
import com.nitrowise.kafkapoc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("userKafkaTemplate")
    private KafkaTemplate<Long, UserMessage> userKafkaTemplate;

    @Autowired
    @Qualifier("transactionalUserKafkaTemplate")
    private KafkaTemplate<Long, UserMessage> transactionalUserKafkaTemplate;

    @Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("nonTxableJmsTemplate")
    private JmsTemplate nonTxableJmsTemplate;

    private Faker faker = new Faker();

    @Transactional
    public void modifyUserAndSendEvent(long id, boolean withTransactionalKafkaTemplate) {
        UserEntity user = userRepository.findById(id).orElse(null);
        String newName = faker.name().fullName();
        log.info("modify user[{}] > old:{} new:{}", id, user.getName(), newName);
        user.setName(newName);
        UserMessage userMessage = new UserMessage((int) user.getId(), user.getName(), user.getFavouriteNumber(), user.getFavouriteColor());

        if (withTransactionalKafkaTemplate) {
            transactionalUserKafkaTemplate.send(userKafkaTemplate.getDefaultTopic(), (long) userMessage.getId(), userMessage);
            jmsTemplate.send("TESZT_TX_QUEUE", session -> session.createTextMessage(user.getId() + " " + user.getName()));
        } else {
            userKafkaTemplate.send(userKafkaTemplate.getDefaultTopic(), (long) userMessage.getId(), userMessage);
            nonTxableJmsTemplate.send("TESZT_WITHOUT_TX_QUEUE", session -> session.createTextMessage(user.getId() + " " + user.getName()));
        }

        if (id % 2 == 0) {
            throw new RuntimeException("TEST");
        }
    }

    @Transactional
    public void generateUsers(int count) {
        log.info("generate users > {}", count);
        for (int i = 0; i < count; i++) {
            UserEntity user = new UserEntity();
            user.setName(faker.name().fullName());
            user.setFavouriteColor(faker.color().name());
            user.setFavouriteNumber(faker.number().numberBetween(1, 21));
            userRepository.save(user);
        }
    }
}
