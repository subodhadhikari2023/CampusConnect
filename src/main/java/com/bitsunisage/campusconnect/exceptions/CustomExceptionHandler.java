package com.bitsunisage.campusconnect.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the application.
 * Intercepts exceptions thrown from any {@code @Controller} and renders
 * a consistent error view with the correct HTTP status rather than exposing
 * stack traces to the user.
 */
@ControllerAdvice
public class CustomExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private static ModelAndView errorView(int status, String message) {
        ModelAndView mav = new ModelAndView("securityViewPages/errorStatus");
        mav.setStatus(HttpStatus.valueOf(status));
        mav.addObject("error", new ErrorResponse(status, message));
        return mav;
    }

    /**
     * Handles resource-not-found exceptions thrown when a required entity is absent.
     * Returns HTTP 404 and renders the error status view with the exception message.
     *
     * @param e the {@link ResourceNotFoundException} that was thrown
     * @return {@link ModelAndView} with HTTP 404 and the not-found message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return errorView(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    /**
     * Handles access-denied scenarios raised by the application itself.
     * Returns HTTP 403 and renders the error status view.
     *
     * @return {@link ModelAndView} with HTTP 403
     */
    @ExceptionHandler(AccessDeniedCustomException.class)
    public ModelAndView handleAccessDeniedCustomException() {
        return errorView(HttpStatus.FORBIDDEN.value(), "You do not have permission to access this resource.");
    }

    /**
     * Handles missing static resources (e.g. browser favicon.ico auto-requests).
     * Returns HTTP 404 without logging a stack trace, since these are routine misses.
     *
     * @param e the {@link NoResourceFoundException} that was thrown
     * @return {@link ModelAndView} with HTTP 404
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoStaticResource(NoResourceFoundException e) {
        log.debug("Static resource not found: {}", e.getResourcePath());
        return errorView(HttpStatus.NOT_FOUND.value(), "Resource not found.");
    }

    /**
     * Catch-all handler for any unhandled {@link Exception}.
     * Logs the full stack trace so the error is diagnosable, then returns
     * HTTP 500 and renders the error status view with a generic message.
     *
     * @param e the exception that was thrown
     * @return {@link ModelAndView} with HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e) {
        log.error("Unhandled exception", e);
        return errorView(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.");
    }
}
