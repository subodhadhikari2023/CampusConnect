package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.CourseDetails;
import com.bitsunisage.campusconnect.project.entities.Department;
import com.bitsunisage.campusconnect.project.entities.Semester;
import com.bitsunisage.campusconnect.project.entities.SubjectDetails;
import com.bitsunisage.campusconnect.project.service.StorageService;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;


@Controller
public class TeacherController {
    private final StorageService storageService;
    private final UserService userService;


    @Autowired
    public TeacherController(StorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;

    }


    @GetMapping("/teacher")
    public String homePage() {
        return "teacherViewPages/indexteacher";
    }

//    Previous method to upload the file to the server local file system
//    @PostMapping("teacher/upload")
//    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//        storageService.uploadToFileSystem(new FileUploadDTO());
//        return "teacherViewPages/uploadResult";
//    }

    //    Later version of method to upload the file to the server local file system using the Data Transfer Object
    @PostMapping("/teacher/upload")
    public String uploadFile(@ModelAttribute FileUploadDTO fileUploadDTO) throws IOException {

        storageService.uploadToFileSystem(fileUploadDTO);
        return "redirect:/teacher";


    }

    @GetMapping("/teacher/uploadPPTs")
    public String uploadPPTs(Model model){
        List<Department> departmentsList = userService.getAllDepartments();
        List<CourseDetails> courseDetailsList = userService.getAllCourses();
        List<Semester> semesterList = userService.getAllSemesters();
        List<SubjectDetails> subjectDetailsList = userService.getAllSubjects();
        model.addAttribute("departments", departmentsList);
        model.addAttribute("courses", courseDetailsList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("subjectList", subjectDetailsList);
        model.addAttribute("fileUploadDTO", new FileUploadDTO());
        return "teacherViewPages/uploadPPTs";
    }

    @GetMapping("/teacher/uploadNotes")
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
