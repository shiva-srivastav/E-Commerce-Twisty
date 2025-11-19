package com.twisty.dto.orderDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderView {
    private Long orderId;
    private Long userId;
    private String orderStatus;
    private String paymentStatus;
    private String  paymentMethod;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemView> items;
}
