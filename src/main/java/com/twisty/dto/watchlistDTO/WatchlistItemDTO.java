package com.twisty.dto.watchlistDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WatchlistItemDTO {
@NotNull
private Long productId;
}
