package com.twisty.repository;

import com.twisty.model.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class InMemoryCartStore {
    private final Map<Long, Cart> carts = new ConcurrentHashMap<>();

    public Cart getOrCreate(long userId){
        return carts.computeIfAbsent(userId, id->new Cart());
    }

    public void clear(long userId){
        carts.remove(userId);
    }
}
