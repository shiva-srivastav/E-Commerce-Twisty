package com.twisty.service;

import com.twisty.dto.cartDTO.CartResponse;
import com.twisty.dto.cartDTO.CartView;

public interface CartService {
    CartResponse addOrUpdate(long userId, long productId, int quantity);
CartView get(long userId);
void remove(long userId,long productId);
void clear(long userId);
    CartResponse setQuantity(long userId, Long productId, int quantity);
    CartResponse decreaseQuantity(long userId, Long productId, int decrease);
}
