package com.twisty.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "watchlist_items")
@Data
public class WatchlistItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    @Column(nullable = false)
    private Long productId;
}
