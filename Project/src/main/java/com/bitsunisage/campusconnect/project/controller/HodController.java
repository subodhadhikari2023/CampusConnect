package com.bitsunisage.campusconnect.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HodController {

    //    Mapping for the Head of the Department home page
    @GetMapping("/hod")
    public String homePage() {
        return "hodViewPages/hod";
    }

    @GetMapping("/hodViewPages/add-course")
    public String showAddCoursePage() {
        return "hodViewPages/add-course";  // Return the Thymeleaf template name
    }
    @GetMapping("/hodViewPages/manage-course")
    public String showManageCoursePage() {
        return "hodViewPages/manage-course";  // Return the Thymeleaf template name
    }
}
