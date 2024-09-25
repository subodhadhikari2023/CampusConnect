package com.bitsunisage.campusconnect.project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AccessDeniedCustomException.class)
    public ModelAndView handleAccessDeniedCustomException() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        errorResponse.setErrorMessage("Not enough accessibility!!!");
//        System.out.println(errorResponse);
        ModelAndView modelAndView = new ModelAndView("securityViewPages/errorStatus");
        modelAndView.addObject("error", errorResponse);
//        System.out.println(modelAndView);
        return modelAndView;
    }


    @ExceptionHandler(Exception.class)
   public ModelAndView handleGenericException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setErrorMessage("Some internal error occurred!");
        ModelAndView modelAndView = new ModelAndView("securityViewPages/errorStatus");
        modelAndView.addObject("error", errorResponse);
//        System.out.println(modelAndView);
        return modelAndView;
    }


}
