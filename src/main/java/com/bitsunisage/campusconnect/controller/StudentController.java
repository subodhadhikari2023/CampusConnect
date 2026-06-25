package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.dto.FileUploadDTO;
import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * Handles HTTP requests for the Student role.
 * Provides browse and search views for each resource type, a filter POST endpoint,
 * and three download endpoints (original, gzip, zip).
 * All routes under {@code /student/**} require {@code ROLE_STUDENT} authority.
 */
@Controller
public class StudentController {

    private final UserService userService;
    private final StorageService storageService;

    /**
     * @param userService    service for catalogue lookups
     * @param storageService service for file retrieval and compression
     */
    public StudentController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    /**
     * Populates the model with all catalogue dropdowns required by search and upload forms.
     * Shared with {@link TeacherController} via a static call.
     *
     * @param model       the Spring MVC model to populate
     * @param userService service used to fetch catalogue data
     */
    static void formModelFeeding(Model model, UserService userService) {
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("departments", userService.getAllDepartments());
        model.addAttribute("courses", userService.getAllCourses());
        model.addAttribute("semesterList", userService.getAllSemesters());
        model.addAttribute("subjectList", userService.getAllSubjects());
    }

    /**
     * Renders the student dashboard home page, including any announcements posted by the
     * HOD of the student's department.
     *
     * @param model populated with {@code announcements} for the student's department
     * @return Thymeleaf template {@code studentViewPages/indexstudent}
     */
    @GetMapping("/student")
    public String getStudent(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userService.findUserByUserId(username);
        if (student != null && student.getDeptID() != null) {
            model.addAttribute("announcements", userService.getAnnouncementsByDeptId(student.getDeptID()));
        }
        return "studentViewPages/indexstudent";
    }

    /**
     * Renders the PPT search page pre-populated with {@code fileRole = "PPTs"}.
     *
     * @param model populated with catalogue dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/searchppts}
     */
    @GetMapping("student/PPTs")
    public String PPTs(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("PPTs");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", dto);
        return "studentViewPages/searchppts";
    }

    /**
     * Renders the notes search page pre-populated with {@code fileRole = "Notes"}.
     *
     * @param model populated with catalogue dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/searchppts}
     */
    @GetMapping("/student/notes")
    public String notes(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Notes");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", dto);
        return "studentViewPages/searchppts";
    }

    /**
     * Renders the programs search page pre-populated with {@code fileRole = "Programs"}.
     *
     * @param model populated with catalogue dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/searchppts}
     */
    @GetMapping("/student/programs")
    public String programs(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Programs");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", dto);
        return "studentViewPages/searchppts";
    }

    /**
     * Renders the audiobooks search page pre-populated with {@code fileRole = "AudioBooks"}.
     *
     * @param model populated with catalogue dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/searchppts}
     */
    @GetMapping("/student/audiobooks")
    public String audiobooks(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("AudioBooks");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", dto);
        return "studentViewPages/searchppts";
    }

    /**
     * Renders the reference books search page pre-populated with {@code fileRole = "ReferenceBook"}.
     *
     * @param model populated with catalogue dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/searchppts}
     */
    @GetMapping("/student/referencebooks")
    public String reference(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("ReferenceBook");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", dto);
        return "studentViewPages/searchppts";
    }

    /**
     * Renders the videos search page pre-populated with {@code fileRole = "Videos"}.
     *
     * @param model populated with catalogue dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/searchppts}
     */
    @GetMapping("/student/video")
    public String video(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Videos");
        formModelFeeding(model);
        model.addAttribute("fileUploadDTO", dto);
        return "studentViewPages/searchppts";
    }

    /**
     * Accepts a filter form submission and renders the matching file list.
     * All five filter fields are required; partial filtering is not currently supported.
     *
     * @param fileUploadDTO bound from the search form; carries all five filter values
     * @param model         populated with the filtered file list and resolved name lists
     * @return Thymeleaf template {@code studentViewPages/PPTs}
     */
    @PostMapping("/student/PPTs/fetchData")
    public String fetchData(@ModelAttribute FileUploadDTO fileUploadDTO, Model model) {
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
        return "studentViewPages/PPTs";
    }

    /**
     * Streams the original file as an octet-stream download attachment.
     * Returns 404 if the record or file on disk does not exist.
     *
     * @param fileId primary key of the {@link FileData} record
     * @return 200 with the raw file bytes, or 404 if the record or file is missing
     */
    @GetMapping("/student/download/original/{fileId}")
    public ResponseEntity<Resource> downloadOriginalFile(@PathVariable Long fileId) {
        Optional<FileData> fileData = storageService.getFileById(fileId);
        if (fileData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        File file = new File(fileData.get().getFilePath(), fileData.get().getFileName());
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileData.get().getFileName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Compresses the file with GZIP and streams it as a download attachment.
     * The compressed output is fully buffered in memory before being sent.
     * Returns 404 if the record or file is missing, or 500 on compression failure.
     *
     * @param fileId primary key of the {@link FileData} record
     * @return 200 with gzip-compressed bytes, 404 if missing, or 500 on error
     */
    @GetMapping("/student/download/gzip/{fileId}")
    public ResponseEntity<Resource> downloadGzipFile(@PathVariable Long fileId) {
        Optional<FileData> fileData = storageService.getFileById(fileId);
        if (fileData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        File file = new File(fileData.get().getFilePath(), fileData.get().getFileName());
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            InputStream compressedStream = storageService.compressFileWithGzip(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(compressedStream.readAllBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileData.get().getFileName() + ".gz\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/gzip");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Compresses the file with ZIP and streams it as a download attachment.
     * The compressed output is fully buffered in memory before being sent.
     * Returns 404 if the record or file is missing, or 500 on compression failure.
     *
     * @param fileId primary key of the {@link FileData} record
     * @return 200 with zip-compressed bytes, 404 if missing, or 500 on error
     */
    @GetMapping("/student/download/zip/{fileId}")
    public ResponseEntity<Resource> downloadZipFile(@PathVariable Long fileId) {
        Optional<FileData> fileData = storageService.getFileById(fileId);
        if (fileData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        File file = new File(fileData.get().getFilePath(), fileData.get().getFileName());
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            InputStream zipStream = storageService.compressFileWithZip(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(zipStream.readAllBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileData.get().getFileName() + ".zip\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Renders the student's own profile page.
     *
     * @param model populated with the current student's {@link User} record
     * @return Thymeleaf template {@code studentViewPages/profile}
     */
    @GetMapping("/student/profile")
    public String studentProfile(Model model) {
        model.addAttribute("student", getLoggedInUser());
        return "studentViewPages/profile";
    }

    /**
     * Saves profile edits submitted by the student.
     * Only allows updating fields that belong to the authenticated user.
     *
     * @param updatedUser form-bound user with new field values
     * @param redirectAttributes carries a success flash on redirect
     * @return redirect to {@code /student/profile}
     */
    @PostMapping("/student/save-profile")
    public String saveStudentProfile(@ModelAttribute User updatedUser,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User current = getLoggedInUser();
        if (!current.getUserId().equals(updatedUser.getUserId())) {
            return "redirect:/student/profile";
        }
        current.setEmail(updatedUser.getEmail());
        userService.save(current);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/student/profile";
    }

    /** Returns the {@link User} entity for the currently authenticated student. */
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUserId(username);
    }

    /** Delegates to the static overload using the injected {@code userService}. */
    private void formModelFeeding(Model model) {
        formModelFeeding(model, userService);
    }
}
