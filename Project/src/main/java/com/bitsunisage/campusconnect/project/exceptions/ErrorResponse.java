package com.bitsunisage.campusconnect.project.exceptions;

/**
 * Carries the HTTP status code and a human-readable message used by error views.
 * Instances are built inside {@link CustomExceptionHandler} and passed to the
 * Thymeleaf error template as a model attribute.
 */
public class ErrorResponse {

    private int statusCode;
    private String errorMessage;

    /** Creates an empty {@code ErrorResponse}; set fields before passing to a view. */
    public ErrorResponse() {
    }

    /**
     * Creates a fully initialised {@code ErrorResponse}.
     *
     * @param statusCode   HTTP status code (e.g. 403, 500)
     * @param errorMessage human-readable description of the error
     */
    public ErrorResponse(int statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }

    /** @return HTTP status code */
    public int getStatusCode() {
        return statusCode;
    }

    /** @param statusCode HTTP status code to set */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /** @return human-readable error description */
    public String getErrorMessage() {
        return errorMessage;
    }

    /** @param errorMessage human-readable error description to set */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "statusCode=" + statusCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
