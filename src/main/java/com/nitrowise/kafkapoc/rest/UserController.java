package com.nitrowise.kafkapoc.rest;

import com.nitrowise.kafkapoc.model.UserDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable("userId") long userId) {
        log.debug("get user > id:{}", userId);
        UserDTO user = new UserDTO();
        user.setId(userId);
        return user;
    }

}
