package com.twisty.dto;

import lombok.Data;

@Data
public class CartResponse {
    private CartView cart;
    private String message;
    private String status;
}
