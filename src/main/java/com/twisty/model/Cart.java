package com.twisty.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Cart {
    private final Map<Long, Integer> items = new ConcurrentHashMap<>();
}
