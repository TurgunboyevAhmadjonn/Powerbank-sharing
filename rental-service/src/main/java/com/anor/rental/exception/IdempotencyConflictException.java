// src/main/java/com/anor/rental/exception/IdempotencyConflictException.java
package com.anor.rental.exception;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String key) {
        super("Idempotency key conflict: different payload for key " + key);
    }
}
