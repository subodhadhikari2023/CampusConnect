package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.DataTransferObject.FileUploadDTO;
import com.bitsunisage.campusconnect.project.entities.*;
import com.bitsunisage.campusconnect.project.service.StorageService;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.util.List;
import java.util.Optional;

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
        List<User> userList = userService.findAllUsers();
        List<CourseDetails> courseDetailsList = userService.getAllCourses();
        List<Semester> semesterList = userService.getAllSemesters();
        List<SubjectDetails> subjectDetailsList = userService.getAllSubjects();
        model.addAttribute("userList", userList);
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
        fileUploadDTO.setFileRole("PPTs");
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

    @GetMapping("/student/programs")
    public String programs(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("Programs");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/student/audiobooks")
    public String audiobooks(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("AudioBooks");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/student/referencebooks")
    public String reference(Model model) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFileRole("ReferenceBook");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        return "studentViewPages/searchppts";
    }

    @GetMapping("/student/video")
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
    private String fetchData(@ModelAttribute FileUploadDTO fileUploadDTO, Model model) {
        List<FileData> fileDataList = storageService.findFilesByFilters(
                fileUploadDTO.getDepartmentId(),
                fileUploadDTO.getCourseId(),
                fileUploadDTO.getSemesterId(),
                fileUploadDTO.getSubjectId(),
                fileUploadDTO.getFileRole()
        );
        formModelFeeding(model);
        TeacherController.feedFileDataToModel(model, fileDataList, userService);
        model.addAttribute("fileDataList", fileDataList);
        System.out.println(fileDataList);
        return "studentViewPages/PPTs";
    }



    @GetMapping("/student/download/original/{fileId}")
    public ResponseEntity<Resource> downloadOriginalFile(@PathVariable Long fileId) {
        Optional<FileData> fileData = storageService.getFileById(fileId);
        File file = new File(fileData.get().getFilePath(), fileData.get().getFileName());

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.get().getFileName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // or specify the file type, e.g., MediaType.APPLICATION_PPTX
                .body(resource);
    }

    @GetMapping("/student/download/gzip/{fileId}")
    public ResponseEntity<Resource> downloadGzipFile(@PathVariable Long fileId) {
        Optional<FileData> fileData = storageService.getFileById(fileId);
        File file = new File(fileData.get().getFilePath(), fileData.get().getFileName());

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            InputStream compressedFileStream = storageService.compressFileWithGzip(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(compressedFileStream.readAllBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.get().getFileName() + ".gz\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/gzip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/student/download/zip/{fileId}")
    public ResponseEntity<Resource> downloadZipFile(@PathVariable Long fileId) {
        Optional<FileData> fileData = storageService.getFileById(fileId);
        File file = new File(fileData.get().getFilePath(), fileData.get().getFileName());

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            InputStream zipFileStream = storageService.compressFileWithZip(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(zipFileStream.readAllBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.get().getFileName() + ".zip\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}