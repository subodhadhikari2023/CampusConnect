package com.bitsunisage.campusconnect.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherController {

    //    Mapping for the Head of the Department home page
    @GetMapping("/teacher")
    public String homePage() {
        return "teacherViewPages/indexteacher";
    }
}
