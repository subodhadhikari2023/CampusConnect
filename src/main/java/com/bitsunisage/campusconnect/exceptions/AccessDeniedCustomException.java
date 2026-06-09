package com.bitsunisage.campusconnect.exceptions;

/**
 * Thrown when an authenticated user accesses a resource they are not permitted to see.
 * Handled by {@link CustomExceptionHandler}, which renders a 403 error view.
 */
public class AccessDeniedCustomException extends RuntimeException {
}
