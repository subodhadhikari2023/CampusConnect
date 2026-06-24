package com.bitsunisage.campusconnect.exceptions;

/**
 * Thrown when a semester lookup returns no result in a context where
 * the semester's existence is a precondition for an operation.
 * Handled by {@link CustomExceptionHandler} as a 404 response.
 */
public class SemesterNotFoundException extends ResourceNotFoundException {

    /**
     * @param semesterId the semester ID that could not be found
     */
    public SemesterNotFoundException(long semesterId) {
        super("Semester not found: " + semesterId);
    }
}
