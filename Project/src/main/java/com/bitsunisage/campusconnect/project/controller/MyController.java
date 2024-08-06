package com.bitsunisage.campusconnect.project.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MyController {

    @GetMapping("/student")
    public String hello2() {

        return "indexstudent";
    }

    @GetMapping("/teacher")
    public String hello3() {
        return "indexteacher";
    }

    @GetMapping("/")
    public String hello(HttpServletRequest request) {
        // Retrieve the URL prior to login from the session attribute
        String redirectURL = (String) request.getSession().getAttribute("url_prior_login");
        if (redirectURL != null) {
            // If the attribute is set, redirect to the stored URL
            return "redirect:" + redirectURL;
        }
        // If the attribute is not set, show the login page

        return "loginfile";
    }
}
