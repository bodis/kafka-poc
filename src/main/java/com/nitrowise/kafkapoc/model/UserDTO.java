package com.nitrowise.kafkapoc.model;

import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String productName;
    private int favoriteNumber;
    private String favoriteColor;
}
