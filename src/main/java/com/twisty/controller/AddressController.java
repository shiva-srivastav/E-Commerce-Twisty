package com.twisty.controller;

import com.twisty.dto.addressDTO.AddressRequest;
import com.twisty.dto.addressDTO.AddressResponse;
import com.twisty.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    private long extratUserId(HttpServletRequest request){
        String header =request.getHeader("X-user-Id");
        if(header == null){
            throw new IllegalArgumentException("X-user-Id header is missing");
        }

        try{
            return Long.parseLong(header);
        } catch(NumberFormatException e){
            throw new IllegalArgumentException("X-user-Id header is invalid");
        }
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> list(HttpServletRequest request){
        long userId = extratUserId(request);
        return ResponseEntity.ok(addressService.list(userId));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(
            @RequestBody AddressRequest body,
            HttpServletRequest request
    ){
        long userId = extratUserId(request);
        return ResponseEntity.ok(addressService.create(userId, body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(
            @PathVariable Long id,
            @RequestBody AddressRequest body,
            HttpServletRequest request
    ){
        long userId = extratUserId(request);
        return ResponseEntity.ok(addressService.update(userId, id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        long userId = extratUserId(request);
        addressService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }


}

