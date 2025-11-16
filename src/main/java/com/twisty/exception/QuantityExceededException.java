package com.twisty.exception;

public class QuantityExceededException extends RuntimeException{
    private final int requestedQuantity;
    private final int availableStock;

    public QuantityExceededException(int requestedQuantity, int availableStock) {
        super("Requested quantity ("+ requestedQuantity +") exceeds stock quantity ("+ availableStock +
                ") exceeds available stock ("+availableStock+")");
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }
    public int getRequestedQuantity() {
        return requestedQuantity;
    }
    public int getAvailableStock() {
        return availableStock;
    }

}
