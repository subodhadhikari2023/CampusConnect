package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.dataAccessObject.UserDAO;
import com.bitsunisage.campusconnect.project.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Controller
public class TeacherController {
    private final UserDAO userDAO;
    private StorageService storageService;

    @Autowired
    public TeacherController(StorageService storageService, UserDAO userDAO) {
        this.storageService = storageService;
        this.userDAO = userDAO;
    }


    @GetMapping("/teacher")
    public String homePage() {
        return "teacherViewPages/indexteacher";
    }

    @PostMapping("teacher/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        storageService.uploadImageToFileSystem(file);
        return "teacherViewPages/uploadResult";
    }

    @GetMapping("/uploadPPTs")
    public String uploadPPTs() {
        return "teacherViewPages/uploadPPTs";
    }
}
