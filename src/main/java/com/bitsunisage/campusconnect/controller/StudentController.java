package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.dto.FileUploadDTO;
import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles HTTP requests for the Student role.
 * Provides a dept-scoped resource browser, three download endpoints, and a profile page.
 * All routes under {@code /student/**} require {@code ROLE_STUDENT} authority.
 */
@Controller
public class StudentController {

    private final UserService userService;
    private final StorageService storageService;

    /**
     * @param userService    service for catalogue lookups and user management
     * @param storageService service for file retrieval and compression
     */
    @Autowired
    public StudentController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /**
     * Renders the student dashboard with announcements from the student's department HOD.
     *
     * @param model populated with {@code announcements} for the student's department
     * @return Thymeleaf template {@code studentViewPages/indexstudent}
     */
    @GetMapping("/student")
    public String getStudent(Model model) {
        User student = getLoggedInUser();
        if (student != null && student.getDeptID() != null) {
            model.addAttribute("announcements",
                    userService.getAnnouncementsByDeptId(student.getDeptID()));
        }
        return "studentViewPages/indexstudent";
    }

    // ── Browse ────────────────────────────────────────────────────────────────

    /**
     * Renders the resource browser pre-scoped to the student's department.
     * An optional {@code category} query parameter pre-selects the file-role filter.
     *
     * @param category optional file-role value to pre-select (e.g. "PPTS", "Notes")
     * @param model    populated with dept-scoped dropdowns and an empty DTO
     * @return Thymeleaf template {@code studentViewPages/browse}
     */
    @GetMapping("/student/browse")
    public String browse(@RequestParam(required = false) String category, Model model) {
        User student = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setDepartmentId(student.getDeptID());
        if (category != null && !category.isBlank()) {
            dto.setFileRole(category);
        }
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model, student);
        return "studentViewPages/browse";
    }

    /**
     * Searches for resources matching the submitted filters.
     * The department ID is always taken from the authenticated student's profile,
     * ignoring whatever value was submitted in the form.
     *
     * @param fileUploadDTO bound search filters from the form
     * @param model         populated with dropdowns, the filter DTO, and the result list
     * @return Thymeleaf template {@code studentViewPages/browse}
     */
    @PostMapping("/student/browse/search")
    public String search(@ModelAttribute FileUploadDTO fileUploadDTO, Model model) {
        User student = getLoggedInUser();
        fileUploadDTO.setDepartmentId(student.getDeptID());
        List<FileData> results = storageService.findFilesByFilters(
                student.getDeptID(),
                fileUploadDTO.getCourseId(),
                fileUploadDTO.getSemesterId(),
                fileUploadDTO.getSubjectId(),
                fileUploadDTO.getFileRole()
        );
        TeacherController.feedFileDataToModel(model, results, userService);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        model.addAttribute("results", results);
        modelFeeding(model, student);
        return "studentViewPages/browse";
    }

    // ── Downloads ─────────────────────────────────────────────────────────────

    /**
     * Streams the original file as an octet-stream download.
     * Returns 404 if the record or the file on disk does not exist.
     *
     * @param fileId primary key of the {@link FileData} record
     * @return 200 with the raw file bytes, or 404 if missing
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
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileData.get().getFileName() + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

    /**
     * Compresses the file with GZIP and streams it as a download attachment.
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
            InputStream stream = storageService.compressFileWithGzip(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
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
            InputStream stream = storageService.compressFileWithZip(file.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(stream.readAllBytes());
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

    // ── Profile ───────────────────────────────────────────────────────────────

    /**
     * Renders the student's profile page.
     * Clears the stored password so it is never pre-filled in the form.
     *
     * @param model populated with the current student's {@link User} record
     * @return Thymeleaf template {@code studentViewPages/profile}
     */
    @GetMapping("/student/profile")
    public String studentProfile(Model model) {
        User student = getLoggedInUser();
        student.setPassword(null);
        model.addAttribute("student", student);
        return "studentViewPages/profile";
    }

    /**
     * Saves the student's updated email and optional new password.
     * A blank new-password field preserves the existing password.
     *
     * @param updatedUser        form-bound user with updated email
     * @param newPassword        new password; blank means keep current
     * @param confirmPassword    must match {@code newPassword} when provided
     * @param redirectAttributes carries flash messages on redirect
     * @return redirect to {@code /student/profile}
     */
    @PostMapping("/student/save-profile")
    public String saveStudentProfile(@ModelAttribute User updatedUser,
                                     @RequestParam("newPassword") String newPassword,
                                     @RequestParam("confirmPassword") String confirmPassword,
                                     RedirectAttributes redirectAttributes) {
        User current = getLoggedInUser();
        if (!current.getUserId().equals(updatedUser.getUserId())) {
            return "redirect:/student/profile";
        }
        if (!newPassword.isBlank() && !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/student/profile";
        }
        current.setEmail(updatedUser.getEmail());
        if (!newPassword.isBlank()) {
            current.setPassword("{noop}" + newPassword);
        }
        userService.save(current);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/student/profile";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the {@link User} entity for the currently authenticated student. */
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUserId(username);
    }

    /**
     * Populates the model with dept-scoped catalogue dropdowns.
     * Only courses, semesters, and subjects belonging to the student's department are loaded.
     *
     * @param model   the Spring MVC model to populate
     * @param student the authenticated student whose department scopes the data
     */
    private void modelFeeding(Model model, User student) {
        List<CourseDetails> courses = userService.getCoursesByDepartmentId(student.getDeptID());
        List<Long> courseIds = courses.stream().map(CourseDetails::getCourseId).toList();
        model.addAttribute("courses", courses);
        model.addAttribute("semesterList", courseIds.isEmpty()
                ? Collections.emptyList()
                : userService.getSemestersByCourseIds(courseIds));
        model.addAttribute("subjectList", courseIds.isEmpty()
                ? Collections.emptyList()
                : userService.getSubjectsByCourseIds(courseIds));
        Department dept = userService.getDepartmentNameByDepartmentId(student.getDeptID().intValue());
        model.addAttribute("departmentName", dept != null ? dept.getName() : "");
    }
}
