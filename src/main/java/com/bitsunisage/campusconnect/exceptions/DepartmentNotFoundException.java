package com.bitsunisage.campusconnect.exceptions;

/**
 * Thrown when a department lookup returns no result in a context where
 * the department's existence is a precondition for an operation.
 * Handled by {@link CustomExceptionHandler} as a 404 response.
 */
public class DepartmentNotFoundException extends ResourceNotFoundException {

    /**
     * @param deptId the numeric department ID that could not be found
     */
    public DepartmentNotFoundException(long deptId) {
        super("Department not found: " + deptId);
    }
}
