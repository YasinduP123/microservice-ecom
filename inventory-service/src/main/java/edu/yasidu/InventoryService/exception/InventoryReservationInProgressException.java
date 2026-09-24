package edu.yasidu.InventoryService.exception;

public class InventoryReservationInProgressException extends RuntimeException {
    public InventoryReservationInProgressException(String message) {
        super(message);
    }
}
