package com.twisty.dto.watchlistDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistItemView {
private Long productId;
private String name;
private BigDecimal price;
private Boolean active;
}
