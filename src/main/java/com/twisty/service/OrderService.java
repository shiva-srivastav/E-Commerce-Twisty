package com.twisty.service;

import com.twisty.dto.orderDTO.OrderSummaryView;
import com.twisty.dto.orderDTO.OrderView;

import java.util.List;

public interface OrderService {
    List<OrderSummaryView> listOrders(long userId);
    OrderView getOrder(long userId, Long orderId);
}
