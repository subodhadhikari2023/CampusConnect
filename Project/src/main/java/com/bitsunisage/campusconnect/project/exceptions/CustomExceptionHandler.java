package com.bitsunisage.campusconnect.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AccessDeniedCustomException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedCustomException e) {
        ModelAndView modelAndView = new ModelAndView("securityViewPages/errorStatus");
        modelAndView.addObject("statusCode", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("errorMessage", "Access Denied: You do not have the required permissions.");
        return modelAndView;
    }


    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception e) {
        ModelAndView modelAndView = new ModelAndView("securityViewPages/errorStatus");
        modelAndView.addObject("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("errorMessage", "An unexpected error occurred: " + e.getMessage());
        return modelAndView;
    }
}
