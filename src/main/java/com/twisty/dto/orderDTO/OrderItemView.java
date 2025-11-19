package com.twisty.dto.orderDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemView {
    private Long productId;
    private String name;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotal;
}
