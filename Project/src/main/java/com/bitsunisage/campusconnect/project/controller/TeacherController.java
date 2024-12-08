package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class TeacherController {
    private StorageService storageService;

    @Autowired
    public TeacherController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("/teacher")
    public String homePage() {
        return "teacherViewPages/indexteacher";
    }

    @PostMapping("teacher/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        storageService.uploadImageToFileSystem(file);
//        model.addAttribute("message", message);
        return "teacherViewPages/uploadResult"; //
    }

    @GetMapping("/uploadPPTs")
    public String uploadPPTs() {
        return "teacherViewPages/uploadPPTs";
    }
    @GetMapping("/uploadNotes")
    public String uploadNotes() {
        return "teacherViewPages/uploadNotes";
    }
    @GetMapping("/uploadsampleprograms")
    public String uploadsampleprograms() {
        return "teacherViewPages/uploadsampleprograms";
    }
    @GetMapping("/uploadaudiobooks")
    public String uploadaudiobooks() {
        return "teacherViewPages/uploadaudiobooks";
    }
    @GetMapping("/uploadReferenceBooks")
    public String uploadReferenceBooks() {
        return "teacherViewPages/uploadReferenceBooks";
    }
    @GetMapping("/uploadVideos")
    public String uploadVideos() {
        return "teacherViewPages/uploadVideos";
    }

}
