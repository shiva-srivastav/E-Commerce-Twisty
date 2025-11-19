package com.twisty.service.impl;

import com.twisty.dto.addressDTO.AddressRequest;
import com.twisty.dto.addressDTO.AddressResponse;
import com.twisty.entity.AddressEntity;
import com.twisty.exception.AddressNotFoundException;
import com.twisty.repository.AddressRepository;
import com.twisty.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public List<AddressResponse> list(long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AddressResponse create(long userId, AddressRequest request) {
        AddressEntity entity = new AddressEntity();
        entity.setUserId(userId);
        applyToEntity(entity,request);
        AddressEntity saved = addressRepository.save(entity);
        return toResponse(saved);
    }



    @Override
    public AddressResponse update(long userId, Long addressId, AddressRequest request) {
        AddressEntity entity = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        if(!entity.getUserId().equals(userId)){
            throw new AddressNotFoundException(addressId);
        }
        applyToEntity(entity,request);
        AddressEntity saved = addressRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void delete(long userId, Long addressId) {
    AddressEntity entity = addressRepository.findById(addressId)
            .orElseThrow(() -> new AddressNotFoundException(addressId));

    if(!entity.getUserId().equals(userId)){
        throw new AddressNotFoundException(addressId);
    }

    addressRepository.delete(entity);
    }

    private void applyToEntity(AddressEntity entity, AddressRequest request) {
        entity.setFullName(request.getFullName());
        entity.setLine1(request.getLine1());
        entity.setLine2(request.getLine2());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setPostalCode(request.getPostalCode());
        entity.setCountry(request.getCountry());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setIsDefault(request.getIsDefault());
    }

    private AddressResponse toResponse(AddressEntity e){
        AddressResponse res=new AddressResponse();
        res.setId(e.getId());
        res.setFullName(e.getFullName());
        res.setLine1(e.getLine1());
        res.setLine2(e.getLine2());
        res.setCity(e.getCity());
        res.setState(e.getState());
        res.setPostalCode(e.getPostalCode());
        res.setCountry(e.getCountry());
        res.setPhoneNumber(e.getPhoneNumber());
        res.setIsDefault(e.getIsDefault());
        return res;
    }
}
