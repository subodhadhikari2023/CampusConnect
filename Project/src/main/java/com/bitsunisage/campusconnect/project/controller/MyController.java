package com.bitsunisage.campusconnect.project.controller;


import com.bitsunisage.campusconnect.project.entity.student.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MyController {

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
public String hello(Model model){
//    Student student = new Student();
//    model.addAttribute("student",student);
    return "loginfile";
}
}
