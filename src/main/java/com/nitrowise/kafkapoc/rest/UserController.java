package com.nitrowise.kafkapoc.rest;

import com.nitrowise.data.avro.UserMessage;
import com.nitrowise.kafkapoc.entity.UserEntity;
import com.nitrowise.kafkapoc.model.UserDTO;
import com.nitrowise.kafkapoc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<Long, UserMessage> userKafkaTemplate;

    private Faker faker = new Faker();


    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable("userId") long userId) {
        log.debug("get user > id:{}", userId);
        UserDTO user = new UserDTO();
        user.setId(userId);
        return user;
    }

    @PostMapping("/generate/{count}")
    public void generateUsers(@PathVariable("count") int count) {
        log.info("generate users > {}", count);
        for (int i = 0; i < count; i++) {
            UserEntity user = new UserEntity();
            user.setName(faker.name().fullName());
            user.setFavouriteColor(faker.color().name());
            user.setFavouriteNumber(faker.number().numberBetween(1, 21));
            userRepository.save(user);
        }
    }

    @PostMapping("/publish")
    public void publishAllUsersToTopic() {
        userRepository.findAll().forEach(user -> {
            UserMessage userMessage = new UserMessage((int) user.getId(), user.getName(), user.getFavouriteNumber(), user.getFavouriteColor());
            userKafkaTemplate.send(userKafkaTemplate.getDefaultTopic(), (long)userMessage.getId(), userMessage);
        });
    }

}
