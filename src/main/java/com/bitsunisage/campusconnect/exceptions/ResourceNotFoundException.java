package com.bitsunisage.campusconnect.exceptions;

/**
 * Base exception for resource-not-found scenarios (HTTP 404).
 * Subclasses identify the specific resource type that was missing.
 * Handled by {@link CustomExceptionHandler}, which renders a 404 error view.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message human-readable description of which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
