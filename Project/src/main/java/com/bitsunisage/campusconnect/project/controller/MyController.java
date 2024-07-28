package com.bitsunisage.campusconnect.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MyController {

//    @GetMapping("/login")
//    public String hello(Model model) {
//        Student student = new Student();
//        model.addAttribute("student",student);
//
//        return "index";
//    }
@GetMapping("/hello")
public String hello(Model model){
//    Student student = new Student();
//    model.addAttribute("student",student);
    return "home";
}
}
