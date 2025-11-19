package com.twisty.dto.orderDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryView {
    private Long orderId;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
