package com.twisty.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(Long id) {
        super(String.format("Could not find address with id=%d", id));
    }
}
