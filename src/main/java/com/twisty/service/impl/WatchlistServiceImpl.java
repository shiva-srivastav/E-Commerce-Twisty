package com.twisty.service.impl;

import com.twisty.dto.WatchlistItemView;
import com.twisty.dto.WatchlistView;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.entity.ProductEntity;
import com.twisty.entity.WatchlistItemEntity;
import com.twisty.repository.ProductRepository;
import com.twisty.repository.WatchlistRepository;
import com.twisty.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistStore;
    private final ProductRepository productStore;

    @Override
    public WatchlistView addProduct(long userId, long productId) {
     ProductEntity product = productStore.findById(productId)
             .orElseThrow(() -> new ProductNotFoundException(productId));

     if(Boolean.FALSE.equals(product.getActive())){
         throw new IllegalArgumentException("Product is not active. "+productId);
     }

     boolean exists = watchlistStore
             .findByUserId(userId)
             .stream()
             .anyMatch(w -> w.getProductId().equals(productId));

     if(!exists){
         WatchlistItemEntity entity = new WatchlistItemEntity();
         entity.setUserId(userId);
         entity.setProductId(productId);
         watchlistStore.save(entity);
         log.info("User {} added product {} to watchlist",userId,productId);
     }

     return buildView(userId);
    }


    @Override
    public WatchlistView getWatchlist(long userId) {
        return buildView(userId);
    }

    @Override
    public void removeProduct(long userId, long productId) {
        watchlistStore.deleteByUserIdAndProductId(userId, productId);
        log.info("User {} removed product {} from watchlist",userId,productId);
    }

    @Override
    public void clear(long userId) {
     watchlistStore.deleteByUserId(userId);
     log.info("User {} cleared watchlist",userId);
    }

    private WatchlistView buildView(long userId) {
        List<WatchlistItemEntity> productIds = watchlistStore.findByUserId(userId);

        List<WatchlistItemView> items = productIds.stream()
                .map(item -> productStore.findById(item.getProductId()))
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

    private WatchlistItemView mapToView(ProductEntity product) {
        WatchlistItemView item = new WatchlistItemView();
        item.setProductId(product.getId());
        item.setName(product.getName());
        item.setPrice(product.getPrice());
        item.setActive(product.getActive());
        return item;
    }
}
