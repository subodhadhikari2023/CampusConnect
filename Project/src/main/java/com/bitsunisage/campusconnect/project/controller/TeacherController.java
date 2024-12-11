package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.*;
import com.bitsunisage.campusconnect.project.service.StorageService;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.security.Principal;
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
    public String uploadPPTs(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("PPTS");
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        modelFeeding(model);
        return "teacherViewPages/uploadPPTs";
    }

    @GetMapping("/teacher/uploadNotes")
    public String uploadNotes(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Notes");
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        modelFeeding(model);
        return "teacherViewPages/uploadNotes";
    }

    private void modelFeeding(Model model) {
        List<Department> departmentsList = userService.getAllDepartments();
        List<CourseDetails> courseDetailsList = userService.getAllCourses();
        List<Semester> semesterList = userService.getAllSemesters();
        List<SubjectDetails> subjectDetailsList = userService.getAllSubjects();
        model.addAttribute("departments", departmentsList);
        model.addAttribute("courses", courseDetailsList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("subjectList", subjectDetailsList);

    }


    @GetMapping("/teacher/uploadsampleprograms")
    public String uploadsampleprograms(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Programs");
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        modelFeeding(model);
        return "teacherViewPages/uploadsampleprograms";
    }

    @GetMapping("/teacher/uploadaudiobooks")
    public String uploadaudiobooks(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("AudioBooks");
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        modelFeeding(model);
        return "teacherViewPages/uploadaudiobooks";
    }

    @GetMapping("/teacher/uploadReferenceBooks")
    public String uploadReferenceBooks(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("ReferenceBook");
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        modelFeeding(model);
        return "teacherViewPages/uploadReferenceBooks";
    }

    @GetMapping("/teacher/uploadVideos")
    public String uploadVideos(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Videos");
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        modelFeeding(model);
        return "teacherViewPages/uploadVideos";

    }

    @GetMapping("/teacher/viewUploadedResources")
    public String viewUploadedResources(Model model, Principal principal){
        String teacherUsername = principal.getName();

      User user = userService.findUserByUserId(teacherUsername);

        Roles role = userService.findRoleByUserId(user.getUserId());
        System.out.println(user.getUserId());
        System.out.println("Role: "+role.getRole());
        // Now, you can pass both userId and role to your resourceService
        List<FileUploadDTO> resources = userService.findResourcesUploaded(user);

        model.addAttribute("resources", "resources");
        return "/teacherViewPages/viewUploadedResources";
    }


}
