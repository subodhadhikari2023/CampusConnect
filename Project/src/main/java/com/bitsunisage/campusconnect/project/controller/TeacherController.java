package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.*;
import com.bitsunisage.campusconnect.project.service.StorageService;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    static void feedFileDataToModel(Model model, List<FileData> resources, UserService userService) {
        List<Long> courseIds = resources.stream().map(FileData::getCourseId).distinct().toList();
        List<CourseDetails> courseDetails = userService.getCourseName(courseIds);
        List<Long> semesterIds = resources.stream().map(FileData::getSemesterId).distinct().toList();
        List<Semester> semesterList = userService.getSemesterName(semesterIds);
        List<Long> subjectIds = resources.stream().map(FileData::getSubjectId).distinct().toList();
        List<SubjectDetails> subjectDetailsList = userService.getSubjectName(subjectIds);
        List<Long> departmentIds = resources.stream().map(FileData::getFileDepartmentId).distinct().toList();
        List<Department> departmentList = userService.getDepartmentNames(departmentIds);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("subjectList", subjectDetailsList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("courseDetails", courseDetails);
    }

    @GetMapping("/teacher")
    public String homePage() {
        return "teacherViewPages/indexteacher";
    }

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
        StudentController.formModelFeeding(model, userService);

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
    public String viewUploadedResources(Model model) {
        User user = userService.findUserByUserId(storageService.getCurrentOwnersName());
        List<FileData> resources = storageService.findResourcesUploaded(user.getUserId());
        feedFileDataToModel(model, resources, userService);
        model.addAttribute("user", user);
        model.addAttribute("resources", resources);

        return "teacherViewPages/viewUploadedResources";
    }

    @PostMapping("/teacher/deleteResource")
    public String deleteResource(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        storageService.deleteResource(id);
        redirectAttributes.addFlashAttribute("message", "Resource has been deleted successfully.");

        return "redirect:/teacher";
    }


}
