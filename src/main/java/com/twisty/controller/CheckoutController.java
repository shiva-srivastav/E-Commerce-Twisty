package com.twisty.controller;

import com.twisty.dto.checkoutDTO.CheckoutConfirmRequest;
import com.twisty.dto.checkoutDTO.CheckoutPreviewRequest;
import com.twisty.dto.checkoutDTO.CheckoutPreviewResponse;
import com.twisty.dto.orderDTO.OrderView;
import com.twisty.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    private long extractUserId(HttpServletRequest request) {
        String header =request.getHeader("X-User-ID");
        if (header == null) {
            throw new IllegalArgumentException("X-User-ID is header missing");
        }
        try{
            return Long.parseLong(header);
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("X-User-ID is invalid");
        }
    }

    @PostMapping ("/preview")
    public ResponseEntity<CheckoutPreviewResponse> preview(
            @RequestBody CheckoutPreviewRequest body,
            HttpServletRequest request
    ){
        long userId = extractUserId(request);
        return ResponseEntity.ok(checkoutService.preview(userId, body));
    }

    @PostMapping("/confirm")
    public ResponseEntity<OrderView> confirm(
            @RequestBody CheckoutConfirmRequest body,
            HttpServletRequest request
            ){
        long userId = extractUserId(request);
        OrderView order = checkoutService.confirm(userId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }



}
