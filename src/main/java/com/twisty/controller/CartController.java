package com.twisty.controller;

import com.twisty.dto.CartItemDTO;
import com.twisty.dto.CartResponse;
import com.twisty.dto.CartView;
import com.twisty.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private long extractUserId(HttpServletRequest request){
        var header = request.getHeader("X-user-Id");
        if(header == null) throw new IllegalArgumentException("X-user-Id header not found");
        try {
            return Long.parseLong(header);
        }
        catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid X-user-Id header value");
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody CartItemDTO dto, HttpServletRequest request){
        long userId = extractUserId(request);
        var view =cartService.addOrUpdate(userId,dto.getProductId(),dto.getQuantity());
        return ResponseEntity.status(HttpStatus.OK).body(view);
    }

    @GetMapping
    public ResponseEntity<CartView> getCart(HttpServletRequest request){
        long userId = extractUserId(request);
        return ResponseEntity.status(HttpStatus.OK).body(cartService.get(userId));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable long productId, HttpServletRequest request){
        long userId = extractUserId(request);
        cartService.remove(userId,productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(HttpServletRequest request){
        long userId = extractUserId(request);
        cartService.clear(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> setQuantity(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body,
            HttpServletRequest request
    ){
        long userId = extractUserId(request);
        int quantity = body.get("quantity");

        CartResponse view = cartService.setQuantity(userId, productId, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(view);
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartResponse> decreaseQuantity(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int decrease,
            HttpServletRequest request){
        long userId = extractUserId(request);
        CartResponse view=cartService.decreaseQuantity(userId, productId,decrease);
        return  ResponseEntity.status(HttpStatus.OK).body(view);
    }

}
