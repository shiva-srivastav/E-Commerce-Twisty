package com.twisty.service;

import com.twisty.dto.addressDTO.AddressRequest;
import com.twisty.dto.addressDTO.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> list(long userId);
    AddressResponse create(long userId, AddressRequest request);
    AddressResponse update(long userId, Long addressId, AddressRequest request);
    void delete(long userId, Long addressId);
}
