package com.bitsunisage.campusconnect.exceptions;

/**
 * Thrown when a user lookup by ID returns no result in a context where
 * the user's existence is a precondition (e.g. the currently logged-in user's
 * own record, or a referenced user in a system operation).
 * Handled by {@link CustomExceptionHandler} as a 404 response.
 */
public class UserNotFoundException extends ResourceNotFoundException {

    /**
     * @param userId the login username that could not be found
     */
    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
    }
}
