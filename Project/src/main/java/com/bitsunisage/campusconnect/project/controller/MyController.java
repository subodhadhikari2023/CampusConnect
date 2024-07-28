package com.bitsunisage.campusconnect.project.controller;


import com.bitsunisage.campusconnect.project.entity.student.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MyController {

    @GetMapping("/hello")
    public String hello2(Model model) {
        Student student = new Student();
        model.addAttribute("student",student);

        return "index";
    }
@GetMapping("/login")
public String hello(Model model){
    Student student = new Student();
    model.addAttribute("student",student);
    return "/loginfile";
}
}
