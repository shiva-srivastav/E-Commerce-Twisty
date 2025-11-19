package com.twisty.controller;

import com.twisty.dto.orderDTO.OrderSummaryView;
import com.twisty.dto.orderDTO.OrderView;
import com.twisty.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private long extractUserId(HttpServletRequest request){
        String header = request.getHeader("X-User-ID");
        if(header == null){
            throw new IllegalArgumentException("X-User-ID is required");
        }

        try{
            return Long.parseLong(header);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("X-User-ID is invalid");
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryView>> list(HttpServletRequest request) {
        long userId = extractUserId(request);
        return ResponseEntity.ok(orderService.listOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderView> getOrder(
        @PathVariable Long orderId,
        HttpServletRequest request
    ){
        long userId = extractUserId(request);
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }
}
