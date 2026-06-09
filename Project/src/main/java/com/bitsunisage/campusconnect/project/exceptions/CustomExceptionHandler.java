package com.bitsunisage.campusconnect.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global exception handler for the application.
 * Intercepts exceptions thrown from any {@code @Controller} and renders
 * a consistent error view rather than exposing stack traces to the user.
 */
@ControllerAdvice
public class CustomExceptionHandler {

    /**
     * Handles access-denied scenarios raised by the application itself.
     * Returns HTTP 403 and renders the error status view.
     *
     * @return {@link ModelAndView} pointing to the error template with a 403 response body
     */
    @ExceptionHandler(AccessDeniedCustomException.class)
    public ModelAndView handleAccessDeniedCustomException() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setErrorMessage("Not enough accessibility!!!");
        ModelAndView modelAndView = new ModelAndView("securityViewPages/errorStatus");
        modelAndView.addObject("error", errorResponse);
        return modelAndView;
    }

    /**
     * Catch-all handler for any unhandled {@link Exception}.
     * Returns HTTP 500 and renders the error status view.
     *
     * @param e the exception that was thrown
     * @return {@link ModelAndView} pointing to the error template with a 500 response body
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorMessage("Some internal error occurred!");
        ModelAndView modelAndView = new ModelAndView("securityViewPages/errorStatus");
        modelAndView.addObject("error", errorResponse);
        return modelAndView;
    }
}
