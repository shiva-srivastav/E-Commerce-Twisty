package com.twisty.service;

import com.twisty.dto.CartView;

public interface CartService {
CartView addOrUpdate(long userId, long productId, int quantity);
CartView get(long userId);
void remove(long userId,long productId);
void clear(long userId);
CartView setQuantity(long userId, Long productId, int quantity);
CartView decreaseQuantity(long userId, Long productId, int decrease);
}
