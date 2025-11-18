package com.twisty.repository;

import com.twisty.entity.WatchlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface WatchlistRepository extends JpaRepository<WatchlistItemEntity,Long> {

    List<WatchlistItemEntity> findByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserId(Long userId);
}
