package com.twisty.controller;

import com.twisty.dto.watchlistDTO.WatchlistItemDTO;
import com.twisty.dto.watchlistDTO.WatchlistView;
import com.twisty.service.WatchlistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
@Slf4j
public class WatchlistController {
    private final WatchlistService watchlistService;

    private long extractUserId(HttpServletRequest request){
        String header = request.getHeader("X-User-Id");
        if(header == null){
           throw new IllegalArgumentException("Missing X-User-Id");
        }

        try{
            return Long.parseLong(header);
        }catch(NumberFormatException ex){
            throw new IllegalArgumentException("Invalid X-User-Id");
        }
    }

    @PostMapping("/items")
    public ResponseEntity<WatchlistView> addItem (
            @RequestBody @Valid WatchlistItemDTO dto,
            HttpServletRequest request
    ){
        long userId = extractUserId(request);
        WatchlistView view = watchlistService.addProduct(userId, dto.getProductId());
        return  new ResponseEntity<>(view, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<WatchlistView> getWatchlist (HttpServletRequest request){
        long userId = extractUserId(request);
        return ResponseEntity.ok(watchlistService.getWatchlist(userId));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItem (
            @PathVariable Long productId,
            HttpServletRequest request
    ){
        long userId = extractUserId(request);
        watchlistService.removeProduct(userId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWatchlist (HttpServletRequest request){
        long userId = extractUserId(request);
        watchlistService.clear(userId);
        return ResponseEntity.ok().build();
    }
}
