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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles HTTP requests for the Teacher role.
 * Provides file upload, browse/download, resource management, profile, and subject-assignment views.
 * All routes under {@code /teacher/**} require {@code ROLE_TEACHER} authority.
 */
@Controller
public class TeacherController {

    private final StorageService storageService;
    private final UserService userService;

    /**
     * @param storageService service for file upload, deletion, compression, and retrieval
     * @param userService    service for catalogue lookups (courses, semesters, subjects)
     */
    @Autowired
    public TeacherController(StorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    /**
     * Enriches a model with display-name lists for the resource types referenced by a file list.
     * Used by both teacher and student views to resolve numeric IDs into human-readable names.
     *
     * @param model       the Spring MVC model to populate
     * @param resources   list of {@link FileData} records whose IDs need resolving
     * @param userService service used to perform the lookups
     */
    static void feedFileDataToModel(Model model, List<FileData> resources, UserService userService) {
        List<Long> courseIds = resources.stream().map(FileData::getCourseId).distinct().toList();
        List<Long> semesterIds = resources.stream().map(FileData::getSemesterId).distinct().toList();
        List<Long> subjectIds = resources.stream().map(FileData::getSubjectId).distinct().toList();
        List<Long> departmentIds = resources.stream().map(FileData::getFileDepartmentId).distinct().toList();
        model.addAttribute("courseDetails", userService.getCourseName(courseIds));
        model.addAttribute("semesterList", userService.getSemesterName(semesterIds));
        model.addAttribute("subjectList", userService.getSubjectName(subjectIds));
        model.addAttribute("departmentList", userService.getDepartmentNames(departmentIds));
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /**
     * Renders the teacher dashboard home page, including announcements for the teacher's department.
     *
     * @param model populated with {@code announcements} for the teacher's department
     * @return Thymeleaf template {@code teacherViewPages/indexteacher}
     */
    @GetMapping("/teacher")
    public String homePage(Model model) {
        User teacher = getLoggedInUser();
        if (teacher != null && teacher.getDeptID() != null) {
            model.addAttribute("announcements", userService.getAnnouncementsByDeptId(teacher.getDeptID()));
        }
        return "teacherViewPages/indexteacher";
    }

    // ── Upload (6 forms + submit) ─────────────────────────────────────────────

    /**
     * Handles file upload with validation. Verifies the file, department, course, semester,
     * and subject all belong to the authenticated teacher's own department chain.
     *
     * @param fileUploadDTO      form data carrying the file and its classification IDs
     * @param redirectAttributes carries flash messages on redirect
     * @return redirect to the teacher dashboard
     * @throws IOException if the file cannot be written to disk
     */
    @PostMapping("/teacher/upload")
    public String uploadFile(@ModelAttribute FileUploadDTO fileUploadDTO,
                             RedirectAttributes redirectAttributes) throws IOException {
        User teacher = getLoggedInUser();

        if (fileUploadDTO.getFile() == null || fileUploadDTO.getFile().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/teacher";
        }
        if (!teacher.getDeptID().equals(fileUploadDTO.getDepartmentId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid department.");
            return "redirect:/teacher";
        }
        CourseDetails course = userService.getCourseById(fileUploadDTO.getCourseId());
        if (course == null || !course.getDepartmentId().equals(teacher.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid course selection.");
            return "redirect:/teacher";
        }
        Semester semester = userService.getSemesterById(fileUploadDTO.getSemesterId());
        if (semester == null || !semester.getCourseId().equals(fileUploadDTO.getCourseId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid semester selection.");
            return "redirect:/teacher";
        }
        SubjectDetails subject = userService.getSubjectById(fileUploadDTO.getSubjectId());
        if (subject == null || subject.getCourseId() != fileUploadDTO.getCourseId().intValue()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid subject selection.");
            return "redirect:/teacher";
        }
        List<TeacherSubject> assignments = userService.getAssignmentsByTeacherId(teacher.getUserId());
        boolean assigned = assignments.stream()
                .anyMatch(a -> a.getSubjectId().equals(fileUploadDTO.getSubjectId()));
        if (!assigned) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not assigned to teach this subject.");
            return "redirect:/teacher";
        }

        storageService.uploadToFileSystem(fileUploadDTO);
        redirectAttributes.addFlashAttribute("successMessage", "File uploaded successfully.");
        return "redirect:/teacher";
    }

    /**
     * Renders the PPT upload form pre-populated with {@code fileRole = "PPTS"}.
     *
     * @param model populated with the DTO and dept-scoped catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadPPTs}
     */
    @GetMapping("/teacher/uploadPPTs")
    public String uploadPPTs(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("PPTS");
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        uploadModelFeeding(model, teacher);
        return "teacherViewPages/uploadPPTs";
    }

    /**
     * Renders the notes upload form pre-populated with {@code fileRole = "Notes"}.
     *
     * @param model populated with the DTO and dept-scoped catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadNotes}
     */
    @GetMapping("/teacher/uploadNotes")
    public String uploadNotes(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Notes");
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        uploadModelFeeding(model, teacher);
        return "teacherViewPages/uploadNotes";
    }

    /**
     * Renders the sample programs upload form pre-populated with {@code fileRole = "Programs"}.
     *
     * @param model populated with the DTO and dept-scoped catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadsampleprograms}
     */
    @GetMapping("/teacher/uploadsampleprograms")
    public String uploadsampleprograms(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Programs");
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        uploadModelFeeding(model, teacher);
        return "teacherViewPages/uploadsampleprograms";
    }

    /**
     * Renders the audiobooks upload form pre-populated with {@code fileRole = "AudioBooks"}.
     *
     * @param model populated with the DTO and dept-scoped catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadaudiobooks}
     */
    @GetMapping("/teacher/uploadaudiobooks")
    public String uploadaudiobooks(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("AudioBooks");
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        uploadModelFeeding(model, teacher);
        return "teacherViewPages/uploadaudiobooks";
    }

    /**
     * Renders the reference books upload form pre-populated with {@code fileRole = "ReferenceBook"}.
     *
     * @param model populated with the DTO and dept-scoped catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadReferenceBooks}
     */
    @GetMapping("/teacher/uploadReferenceBooks")
    public String uploadReferenceBooks(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("ReferenceBook");
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        uploadModelFeeding(model, teacher);
        return "teacherViewPages/uploadReferenceBooks";
    }

    /**
     * Renders the videos upload form pre-populated with {@code fileRole = "Videos"}.
     *
     * @param model populated with the DTO and dept-scoped catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadVideos}
     */
    @GetMapping("/teacher/uploadVideos")
    public String uploadVideos(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Videos");
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        uploadModelFeeding(model, teacher);
        return "teacherViewPages/uploadVideos";
    }

    // ── View / Delete own resources ───────────────────────────────────────────

    /**
     * Lists all files uploaded by the currently authenticated teacher.
     *
     * @param model populated with the teacher's {@link User}, their resource list, and resolved name lists
     * @return Thymeleaf template {@code teacherViewPages/viewUploadedResources}
     */
    @GetMapping("/teacher/viewUploadedResources")
    public String viewUploadedResources(Model model) {
        User user = getLoggedInUser();
        List<FileData> resources = storageService.findResourcesUploaded(user.getUserId());
        feedFileDataToModel(model, resources, userService);
        model.addAttribute("user", user);
        model.addAttribute("resources", resources);
        return "teacherViewPages/viewUploadedResources";
    }

    /**
     * Deletes a file resource and redirects to the teacher dashboard.
     *
     * @param id                 primary key of the {@link FileData} record to delete
     * @param redirectAttributes carries a success flash message on redirect
     * @return redirect to the teacher dashboard
     */
    @PostMapping("/teacher/deleteResource")
    public String deleteResource(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        storageService.deleteResource(id);
        redirectAttributes.addFlashAttribute("message", "Resource has been deleted successfully.");
        return "redirect:/teacher";
    }

    // ── Browse department resources ───────────────────────────────────────────

    /**
     * Renders the department resource browser. Shows a dept-scoped search form and, after
     * a POST search, the matching results beneath it.
     *
     * @param model populated with dept-scoped dropdowns and an empty DTO
     * @return Thymeleaf template {@code teacherViewPages/browse}
     */
    @GetMapping("/teacher/browse")
    public String browseResources(Model model) {
        User teacher = getLoggedInUser();
        FileUploadDTO dto = new FileUploadDTO();
        dto.setDepartmentId(teacher.getDeptID());
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model, teacher);
        return "teacherViewPages/browse";
    }

    /**
     * Searches department resources by the selected filters and renders results on the same page.
     * Results are scoped to the teacher's own department regardless of the submitted departmentId.
     *
     * @param fileUploadDTO bound search filters
     * @param model         populated with dropdowns, the filter DTO, and the result list
     * @return Thymeleaf template {@code teacherViewPages/browse}
     */
    @PostMapping("/teacher/browse/search")
    public String searchResources(@ModelAttribute FileUploadDTO fileUploadDTO, Model model) {
        User teacher = getLoggedInUser();
        // Always enforce the teacher's own department — ignores any tampered departmentId
        fileUploadDTO.setDepartmentId(teacher.getDeptID());
        List<FileData> results = storageService.findFilesByFilters(
                teacher.getDeptID(),
                fileUploadDTO.getCourseId(),
                fileUploadDTO.getSemesterId(),
                fileUploadDTO.getSubjectId(),
                fileUploadDTO.getFileRole()
        );
        feedFileDataToModel(model, results, userService);
        model.addAttribute("fileUploadDTO", fileUploadDTO);
        model.addAttribute("results", results);
        modelFeeding(model, teacher);
        return "teacherViewPages/browse";
    }

    // ── Download resources ────────────────────────────────────────────────────

    /**
     * Streams the original file as an octet-stream download attachment.
     * Returns 404 if the record or file on disk does not exist.
     *
     * @param fileId primary key of the {@link FileData} record
     * @return 200 with the raw file bytes, or 404 if the record or file is missing
     */
    @GetMapping("/teacher/download/original/{fileId}")
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
    @GetMapping("/teacher/download/gzip/{fileId}")
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
    @GetMapping("/teacher/download/zip/{fileId}")
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
     * Renders the teacher's own profile page.
     * Clears the stored password so it is never pre-filled in the form.
     *
     * @param model populated with the current teacher's {@link User} record
     * @return Thymeleaf template {@code teacherViewPages/profile}
     */
    @GetMapping("/teacher/profile")
    public String teacherProfile(Model model) {
        User teacher = getLoggedInUser();
        teacher.setPassword(null);
        model.addAttribute("teacher", teacher);
        return "teacherViewPages/profile";
    }

    /**
     * Saves the teacher's updated email and optional new password.
     * A blank new-password field preserves the existing password.
     *
     * @param updatedUser        form-bound user with updated email
     * @param newPassword        new password; blank means keep current
     * @param confirmPassword    must match {@code newPassword} when provided
     * @param redirectAttributes carries flash messages on redirect
     * @return redirect to {@code /teacher/profile}
     */
    @PostMapping("/teacher/save-profile")
    public String saveTeacherProfile(@ModelAttribute User updatedUser,
                                     @RequestParam("newPassword") String newPassword,
                                     @RequestParam("confirmPassword") String confirmPassword,
                                     RedirectAttributes redirectAttributes) {
        User current = getLoggedInUser();
        if (!current.getUserId().equals(updatedUser.getUserId())) {
            return "redirect:/teacher/profile";
        }
        if (!newPassword.isBlank() && !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/teacher/profile";
        }
        current.setEmail(updatedUser.getEmail());
        if (!newPassword.isBlank()) {
            current.setPassword("{noop}" + newPassword);
        }
        userService.save(current);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/teacher/profile";
    }

    // ── My Subjects ───────────────────────────────────────────────────────────

    /**
     * Renders the list of subjects assigned to the currently authenticated teacher.
     * Course and semester names are pre-resolved into {@code Integer}-keyed maps so
     * Thymeleaf can look them up with {@code int} properties without a type mismatch.
     *
     * @param model populated with subjects, courseNames, and semesterNames for the assignments
     * @return Thymeleaf template {@code teacherViewPages/my-subjects}
     */
    @GetMapping("/teacher/my-subjects")
    public String mySubjects(Model model) {
        User teacher = getLoggedInUser();
        List<TeacherSubject> assignments = userService.getAssignmentsByTeacherId(teacher.getUserId());
        List<Long> subjectIds = assignments.stream().map(TeacherSubject::getSubjectId).toList();
        List<SubjectDetails> subjects = userService.getSubjectName(subjectIds);
        List<Long> courseIds = subjects.stream().map(s -> (long) s.getCourseId()).distinct().toList();
        List<Long> semesterIds = subjects.stream().map(s -> (long) s.getSemesterId()).distinct().toList();
        Map<Integer, String> courseNames = userService.getCourseName(courseIds).stream()
                .collect(Collectors.toMap(c -> c.getCourseId().intValue(), CourseDetails::getCourseName));
        Map<Integer, String> semesterNames = userService.getSemesterName(semesterIds).stream()
                .collect(Collectors.toMap(s -> s.getSemesterId().intValue(), Semester::getSemesterName));
        model.addAttribute("subjects", subjects);
        model.addAttribute("courseNames", courseNames);
        model.addAttribute("semesterNames", semesterNames);
        return "teacherViewPages/my-subjects";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the {@link User} entity for the currently authenticated teacher. */
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUserId(username);
    }

    /**
     * Populates the model for upload forms with only the subjects the teacher is assigned to teach,
     * along with the courses and semesters that contain those subjects.
     * Prevents the upload form from offering subjects outside the teacher's assignments.
     *
     * @param model   the Spring MVC model to populate
     * @param teacher the authenticated teacher
     */
    private void uploadModelFeeding(Model model, User teacher) {
        List<TeacherSubject> assignments = userService.getAssignmentsByTeacherId(teacher.getUserId());
        List<Long> assignedIds = assignments.stream().map(TeacherSubject::getSubjectId).toList();
        List<SubjectDetails> subjects = assignedIds.isEmpty()
                ? Collections.emptyList()
                : userService.getSubjectName(assignedIds);
        List<Long> courseIds = subjects.stream().map(s -> (long) s.getCourseId()).distinct().toList();
        List<Long> semesterIds = subjects.stream().map(s -> (long) s.getSemesterId()).distinct().toList();
        model.addAttribute("courses", courseIds.isEmpty() ? Collections.emptyList() : userService.getCourseName(courseIds));
        model.addAttribute("semesterList", semesterIds.isEmpty() ? Collections.emptyList() : userService.getSemesterName(semesterIds));
        model.addAttribute("subjectList", subjects);
        Department dept = userService.getDepartmentNameByDepartmentId(teacher.getDeptID().intValue());
        model.addAttribute("departmentName", dept != null ? dept.getName() : "");
    }

    /**
     * Populates the model with dept-scoped catalogue dropdowns for browse forms.
     * Shows all courses, semesters, and subjects in the teacher's department.
     *
     * @param model   the Spring MVC model to populate
     * @param teacher the authenticated teacher whose department scopes the data
     */
    private void modelFeeding(Model model, User teacher) {
        List<CourseDetails> courses = userService.getCoursesByDepartmentId(teacher.getDeptID());
        List<Long> courseIds = courses.stream().map(CourseDetails::getCourseId).toList();
        model.addAttribute("courses", courses);
        model.addAttribute("semesterList", userService.getSemestersByCourseIds(courseIds));
        model.addAttribute("subjectList", userService.getSubjectsByCourseIds(courseIds));
        Department dept = userService.getDepartmentNameByDepartmentId(teacher.getDeptID().intValue());
        model.addAttribute("departmentName", dept != null ? dept.getName() : "");
    }
}
