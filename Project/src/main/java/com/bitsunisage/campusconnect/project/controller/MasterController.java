package com.bitsunisage.campusconnect.project.controller;


import com.bitsunisage.campusconnect.project.entities.User;
import com.bitsunisage.campusconnect.project.exceptions.AccessDeniedCustomException;
import com.bitsunisage.campusconnect.project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MasterController implements ErrorController {
    private UserService userService;

    @Autowired
    public MasterController(UserService userService){
        this.userService=userService;
    }

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
    @GetMapping("/view-data")
    public String viewData(Model model) {

        model.addAttribute("members", userService.findAllUsers());
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("departments", userService.getAllDepartments());
        return "/ReferralData";
    }



}
