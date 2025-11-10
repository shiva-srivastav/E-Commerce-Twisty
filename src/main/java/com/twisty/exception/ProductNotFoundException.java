package com.twisty.exception;

/**
 * Custom exception thrown when a product with a given ID is not found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
