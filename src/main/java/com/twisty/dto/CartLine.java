package com.twisty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartLine {
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;
}
