package com.bitsunisage.campusconnect.project.controller;


import com.bitsunisage.campusconnect.project.entity.student.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.filter.RequestContextFilter;


@Controller
public class MyController {

    private final RequestContextFilter requestContextFilter;

    public MyController(RequestContextFilter requestContextFilter) {
        this.requestContextFilter = requestContextFilter;
    }

    @GetMapping("/student")
    public String hello2(Model model) {
//        Student student = new Student();
//        model.addAttribute("student",student);

        return "indexstudent";
    }

    @GetMapping("/teacher")
    public String hello3(Model model) {
//        Student student = new Student();
//        model.addAttribute("student",student);

        return "indexteacher";
    }

    @GetMapping("/")
    public String hello(HttpServletRequest request, Model model) {
        String redirectURL = (String) request.getSession().getAttribute("url_prior_login");
        if (redirectURL != null) {
            // If the attribute is set, redirect to the stored URL
            return "redirect:" + redirectURL;
        }
        // If the attribute is not set, show the login page
        
//    Student student = new Student();
//    model.addAttribute("student",student);
        return "loginfile";
    }
}
