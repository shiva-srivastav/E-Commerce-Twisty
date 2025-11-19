package com.twisty.dto.watchlistDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistView {
    private Long userId;
    private List<WatchlistItemView> items;
    private int totalItems;
}
