package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.dto.FileUploadDTO;
import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Handles HTTP requests for the Teacher role.
 * Provides file upload, uploaded-resource management, and upload form views.
 * All routes under {@code /teacher/**} require {@code ROLE_TEACHER} authority.
 */
@Controller
public class TeacherController {

    private final StorageService storageService;
    private final UserService userService;

    /**
     * @param storageService service for file upload, deletion, and retrieval
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

    /**
     * Renders the teacher dashboard home page, including any announcements posted by the
     * HOD of the teacher's department.
     *
     * @param model populated with {@code announcements} for the teacher's department
     * @return Thymeleaf template {@code teacherViewPages/indexteacher}
     */
    @GetMapping("/teacher")
    public String homePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User teacher = userService.findUserByUserId(username);
        if (teacher != null && teacher.getDeptID() != null) {
            model.addAttribute("announcements", userService.getAnnouncementsByDeptId(teacher.getDeptID()));
        }
        return "teacherViewPages/indexteacher";
    }

    /**
     * Handles the file upload form submission. Delegates storage to {@link StorageService}
     * and redirects to the dashboard on success.
     *
     * @param fileUploadDTO the bound form data carrying the file and its classification IDs
     * @return redirect to the teacher dashboard
     * @throws IOException if the file cannot be written to disk
     */
    @PostMapping("/teacher/upload")
    public String uploadFile(@ModelAttribute FileUploadDTO fileUploadDTO) throws IOException {
        storageService.uploadToFileSystem(fileUploadDTO);
        return "redirect:/teacher";
    }

    /**
     * Renders the PPT upload form pre-populated with {@code fileRole = "PPTS"}.
     *
     * @param model populated with the DTO and catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadPPTs}
     */
    @GetMapping("/teacher/uploadPPTs")
    public String uploadPPTs(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("PPTS");
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model);
        return "teacherViewPages/uploadPPTs";
    }

    /**
     * Renders the notes upload form pre-populated with {@code fileRole = "Notes"}.
     *
     * @param model populated with the DTO and catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadNotes}
     */
    @GetMapping("/teacher/uploadNotes")
    public String uploadNotes(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Notes");
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model);
        return "teacherViewPages/uploadNotes";
    }

    /**
     * Renders the sample programs upload form pre-populated with {@code fileRole = "Programs"}.
     *
     * @param model populated with the DTO and catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadsampleprograms}
     */
    @GetMapping("/teacher/uploadsampleprograms")
    public String uploadsampleprograms(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Programs");
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model);
        return "teacherViewPages/uploadsampleprograms";
    }

    /**
     * Renders the audiobooks upload form pre-populated with {@code fileRole = "AudioBooks"}.
     *
     * @param model populated with the DTO and catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadaudiobooks}
     */
    @GetMapping("/teacher/uploadaudiobooks")
    public String uploadaudiobooks(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("AudioBooks");
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model);
        return "teacherViewPages/uploadaudiobooks";
    }

    /**
     * Renders the reference books upload form pre-populated with {@code fileRole = "ReferenceBook"}.
     *
     * @param model populated with the DTO and catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadReferenceBooks}
     */
    @GetMapping("/teacher/uploadReferenceBooks")
    public String uploadReferenceBooks(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("ReferenceBook");
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model);
        return "teacherViewPages/uploadReferenceBooks";
    }

    /**
     * Renders the videos upload form pre-populated with {@code fileRole = "Videos"}.
     *
     * @param model populated with the DTO and catalogue dropdowns
     * @return Thymeleaf template {@code teacherViewPages/uploadVideos}
     */
    @GetMapping("/teacher/uploadVideos")
    public String uploadVideos(Model model) {
        FileUploadDTO dto = new FileUploadDTO();
        dto.setFileRole("Videos");
        model.addAttribute("fileUploadDTO", dto);
        modelFeeding(model);
        return "teacherViewPages/uploadVideos";
    }

    /**
     * Lists all files uploaded by the currently authenticated teacher.
     *
     * @param model populated with the teacher's {@link com.bitsunisage.campusconnect.entities.User},
     *              their resource list, and resolved catalogue name lists
     * @return Thymeleaf template {@code teacherViewPages/viewUploadedResources}
     */
    @GetMapping("/teacher/viewUploadedResources")
    public String viewUploadedResources(Model model) {
        User user = userService.findUserByUserId(storageService.getCurrentOwnersName());
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
     * @param redirectAttributes carries a success flash message to the redirected view
     * @return redirect to the teacher dashboard
     */
    @PostMapping("/teacher/deleteResource")
    public String deleteResource(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        storageService.deleteResource(id);
        redirectAttributes.addFlashAttribute("message", "Resource has been deleted successfully.");
        return "redirect:/teacher";
    }

    /** Populates the model with catalogue dropdowns shared by all upload forms. */
    private void modelFeeding(Model model) {
        StudentController.formModelFeeding(model, userService);
    }
}
