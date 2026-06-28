// src/main/java/com/anor/rental/exception/RentalNotFoundException.java
package com.anor.rental.exception;

import java.util.UUID;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(UUID id) {
        super("Rental not found: " + id);
    }
}
