package com.bitsunisage.campusconnect.project.controller;


import com.bitsunisage.campusconnect.project.exceptions.AccessDeniedCustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MasterController implements ErrorController {

    @GetMapping("/")
    public String showPublicLandingPage(){
        return "/index";
    }



    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request) {
        // Retrieve the URL prior to login from the session attribute
        String redirectURL = (String) request.getSession().getAttribute("url_prior_login");
        if (redirectURL != null) {
            // If the attribute is set, redirect to the stored URL
            return "redirect:" + redirectURL;
        }
        // If the attribute is not set, show the login page

        return "/loginFile";
    }


    @GetMapping("/access-denied")
    public void accessDeniedErrorMapping() {
        throw new AccessDeniedCustomException();
    }

    @GetMapping("/error")
    public void error() {
        throw new RuntimeException();
    }


}
