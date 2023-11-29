package com.nitrowise.kafkapoc.model;

import lombok.Data;

@Data
public class OrderDTO {

    private Long id;
    private long userId;
    private long productId;
    private String productName;
    private int price;
    private int count;
}
