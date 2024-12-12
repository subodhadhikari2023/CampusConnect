package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.*;
import com.bitsunisage.campusconnect.project.service.StorageService;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StudentController {

    private final UserService userService;
    private final StorageService storageService;

    public StudentController(UserService userService, StorageService storageService) {

        this.userService = userService;
        this.storageService = storageService;
    }

    static void formModelFeeding(Model model, UserService userService) {
        List<Department> departmentsList = userService.getAllDepartments();
        List<CourseDetails> courseDetailsList = userService.getAllCourses();
        List<Semester> semesterList = userService.getAllSemesters();
        List<SubjectDetails> subjectDetailsList = userService.getAllSubjects();
        model.addAttribute("departments", departmentsList);
        model.addAttribute("courses", courseDetailsList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("subjectList", subjectDetailsList);
    }

    @GetMapping("/student")
    public String getStudent() {

        return "studentViewPages/indexstudent";
    }

    @GetMapping("student/PPTs")
    public String PPTs(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Notes");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);

        return "studentViewPages/searchppts";
    }

    @GetMapping("/student/notes")
    public String notes(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Notes");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/programs")
    public String programs(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Programs");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/audiobooks")
    public String audiobooks(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("AudioBooks");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/referencebooks")
    public String reference(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("ReferenceBooks");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/video")
    public String video(Model model) {

        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Videos");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    private void formModelFeeding(Model model) {
        formModelFeeding(model, userService);

    }

    @PostMapping("/student/PPTs/fetchData")
    private String fetchData(@ModelAttribute FileUploadDTO fileUploadDTO, Model model){
        List<FileData> fileDataList = storageService.findFilesByFilters(
                fileUploadDTO.getDepartmentId(),
                fileUploadDTO.getCourseId(),
                fileUploadDTO.getSemesterId(),
                fileUploadDTO.getSubjectId(),
                fileUploadDTO.getFileRole()
        );
        TeacherController.feedFileDataToModel(model, fileDataList, userService);
        model.addAttribute("fileDataList", fileDataList);
        System.out.println(fileDataList);
        return "studentViewPages/PPTs";
    }
}