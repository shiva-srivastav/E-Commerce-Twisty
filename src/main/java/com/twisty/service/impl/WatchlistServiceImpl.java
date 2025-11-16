package com.twisty.service.impl;

import com.twisty.dto.WatchlistItemView;
import com.twisty.dto.WatchlistView;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.model.Product;
import com.twisty.repository.InMemoryProductStore;
import com.twisty.repository.InMemoryWatchlistStore;
import com.twisty.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private final InMemoryWatchlistStore watchlistStore;
    private final InMemoryProductStore productStore;

    @Override
    public WatchlistView addProduct(long userId, long productId) {
     Product product = productStore.findById(productId)
             .orElseThrow(() -> new ProductNotFoundException(productId));

     if(Boolean.FALSE.equals(product.getActive())){
         throw new IllegalArgumentException("Product is not active. "+productId);
     }

     watchlistStore.addProduct(userId, productId);
     log.info("User {} added product {} to watchlist",userId,productId);

     return buildView(userId);
    }


    @Override
    public WatchlistView getWatchlist(long userId) {
        return buildView(userId);
    }

    @Override
    public void removeProduct(long userId, long productId) {
        watchlistStore.removeProduct(userId, productId);
        log.info("User {} removed product {} from watchlist",userId,productId);
    }

    @Override
    public void clear(long userId) {
     watchlistStore.clear(userId);
     log.info("User {} cleared watchlist",userId);
    }

    private WatchlistView buildView(long userId) {
        Set<Long> productIds = watchlistStore.getProducts(userId);

        List<WatchlistItemView> items = productIds.stream()
                .map(productStore::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p->Boolean.TRUE.equals(p.getActive()))
                .map(this::mapToView)
                .toList();

        WatchlistView view = new WatchlistView();
        view.setUserId(userId);
        view.setItems(items);
        view.setTotalItems(items.size());
    return view;

    }

    private WatchlistItemView mapToView(Product product) {
        WatchlistItemView item = new WatchlistItemView();
        item.setProductId(product.getId());
        item.setName(product.getName());
        item.setPrice(product.getPrice());
        item.setActive(product.getActive());
        return item;
    }
}
