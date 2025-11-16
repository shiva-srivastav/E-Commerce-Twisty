package com.twisty.service;

import com.twisty.dto.WatchlistView;

public interface WatchlistService {
    WatchlistView addProduct(long userId, long productId);
    WatchlistView getWatchlist(long userId);
    void removeProduct(long userId, long productId);
    void clear(long userId);
}
