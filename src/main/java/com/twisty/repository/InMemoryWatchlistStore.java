package com.twisty.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Slf4j
public class InMemoryWatchlistStore {
private final Map<Long, Set<Long>> store = new ConcurrentHashMap<>();

public Set<Long> getOrCreate(long userId){
    return store.computeIfAbsent(userId, id-> ConcurrentHashMap.newKeySet());
}

public void addProduct(long userId, long productId){
    getOrCreate(userId).add(productId);
    log.debug("Added product {} to watchlist {}", productId, userId);
}

public void removeProduct(long userId, long productId){
    getOrCreate(userId).remove(productId);
    log.debug("Removed product {} from watchlist {}", productId, userId);
}

public void clear(long userId){
    store.remove(userId);
    log.debug("Cleared watchlist {}", userId);
}

public Set<Long> getProducts(long userId){
    return new HashSet<>(getOrCreate(userId));
}
}
