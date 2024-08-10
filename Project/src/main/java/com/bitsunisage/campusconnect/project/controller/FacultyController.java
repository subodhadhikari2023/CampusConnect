package com.bitsunisage.campusconnect.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FacultyController {
    @GetMapping("/teacher")
    public String hello3() {
        return "indexteacher";
    }
}
