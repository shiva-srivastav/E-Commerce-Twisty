package com.twisty.service.impl;

import com.twisty.dto.orderDTO.OrderItemView;
import com.twisty.dto.orderDTO.OrderSummaryView;
import com.twisty.dto.orderDTO.OrderView;
import com.twisty.entity.OrderEntity;
import com.twisty.exception.OrderNotFoundException;
import com.twisty.repository.OrderRepository;
import com.twisty.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryView> listOrders(long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this :: toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderView getOrder(long userId, Long orderId) {
        OrderEntity o = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(!o.getUserId().equals(userId)) {
            throw new OrderNotFoundException(orderId);
        }

        return toOrderView(o);

    }

    private OrderView toOrderView(OrderEntity o) {
        List<OrderItemView> items = o.getItems().stream()
                .map(oi -> new OrderItemView(
                        oi.getProductId(),
                        oi.getProductName(),
                        oi.getUnitPrice(),
                        oi.getQuantity(),
                        oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity()))
                ))
                .toList();

        return new OrderView(
                o.getId(),
                o.getUserId(),
                o.getOrderStatus(),
                o.getPaymentStatus(),
                o.getPaymentMethod(),
                o.getSubtotal(),
                o.getShippingFee(),
                o.getTaxAmount(),
                o.getTotalAmount(),
                o.getCreatedAt(),
                items
        );
    }

    private OrderSummaryView toSummary(OrderEntity order) {
        return new OrderSummaryView(
                order.getId(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }
}
