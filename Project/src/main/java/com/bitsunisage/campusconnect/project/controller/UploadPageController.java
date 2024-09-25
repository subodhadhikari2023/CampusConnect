package com.bitsunisage.campusconnect.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadPageController {

    @GetMapping("teacherViewPages/uploadPPTs")
    public String showUploadPPTForm() {
        return "teacherViewPages/uploadPPTs";  // Render the uploadPPTs.html form page
    }

    @GetMapping("/teacherViewPages/uploadNotes")
    public String showUploadNotesForm() {
        return "teacherViewPages/uploadNotes";
    }
}


