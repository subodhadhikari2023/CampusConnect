package com.bitsunisage.campusconnect.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {
    @GetMapping("/student")
    public String getStudent() {

        return "studentViewPages/indexstudent";
    }
    @GetMapping("/PPTs")
    public String PPTs() {

        return "studentViewPages/searchppts";
    }
    @GetMapping("/notes")
    public String notes() {

        return "studentViewPages/searchnotes";
    }
    @GetMapping("/programs")
    public String programs() {

        return "studentViewPages/searchprograms";
    }
    @GetMapping("/audiobooks")
    public String audiobooks() {

        return "studentViewPages/searchaudiobooks";
    }
    @GetMapping("/referencebooks")
    public String reference() {

        return "studentViewPages/searchreferencebooks";
    }
    @GetMapping("/video")
    public String video() {

        return "studentViewPages/searchvideos";
    }

}
