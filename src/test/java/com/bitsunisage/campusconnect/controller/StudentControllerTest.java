package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.config.TestWebConfig;
import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link WebMvcTest} slice for {@link StudentController}.
 * All tests run as student1 (ROLE_STUDENT).
 */
@WebMvcTest(StudentController.class)
@Import(TestWebConfig.class)
@WithMockUser(username = "student1", roles = "STUDENT")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StorageService storageService;

    private User studentUser;
    private CourseDetails ownedCourse;
    private Semester ownedSemester;
    private SubjectDetails ownedSubject;
    private Department dept;

    @BeforeEach
    void setUp() {
        studentUser = new User();
        studentUser.setUserId("student1");
        studentUser.setDeptID(1001L);
        studentUser.setDepartment("Computer Applications");
        studentUser.setEmail("student1@test.edu");

        ownedCourse = new CourseDetails();
        ownedCourse.setCourseId(1L);
        ownedCourse.setDepartmentId(1001L);
        ownedCourse.setCourseName("BCA");

        ownedSemester = new Semester();
        ownedSemester.setSemesterId(1L);
        ownedSemester.setCourseId(1L);
        ownedSemester.setSemesterName("Semester I");

        ownedSubject = new SubjectDetails();
        ownedSubject.setSubjectId(1L);
        ownedSubject.setCourseId(1);
        ownedSubject.setSemesterId(1);
        ownedSubject.setSubjectName("Data Structures");

        dept = new Department();
        dept.setId(1001L);
        dept.setName("Computer Applications");

        lenient().when(userService.findUserByUserId("student1")).thenReturn(studentUser);
        lenient().when(userService.getCoursesByDepartmentId(1001L)).thenReturn(Collections.emptyList());
        lenient().when(userService.getSemestersByCourseIds(any())).thenReturn(Collections.emptyList());
        lenient().when(userService.getSubjectsByCourseIds(any())).thenReturn(Collections.emptyList());
        lenient().when(userService.getDepartmentNameByDepartmentId(1001)).thenReturn(dept);
        lenient().when(userService.getCourseName(any())).thenReturn(Collections.emptyList());
        lenient().when(userService.getSemesterName(any())).thenReturn(Collections.emptyList());
        lenient().when(userService.getSubjectName(any())).thenReturn(Collections.emptyList());
        lenient().when(userService.getDepartmentNames(any())).thenReturn(Collections.emptyList());
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    @Test
    void studentHomeReturnsStudentView() throws Exception {
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/indexstudent"));
    }

    @Test
    void studentHomePopulatesAnnouncementsForStudentsDept() throws Exception {
        Announcement a = new Announcement();
        a.setTitle("Test Announcement");
        when(userService.getAnnouncementsByDeptId(1001L)).thenReturn(List.of(a));

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("announcements"));
    }

    // ── Browse GET ────────────────────────────────────────────────────────────

    @Test
    void browsePageReturnsBrowseViewWithDeptScopedDropdowns() throws Exception {
        mockMvc.perform(get("/student/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/browse"))
                .andExpect(model().attributeExists("fileUploadDTO", "courses",
                        "semesterList", "subjectList", "departmentName"));
    }

    @Test
    void browsePageWithCategoryParamPreSelectsFileRole() throws Exception {
        mockMvc.perform(get("/student/browse").param("category", "Notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/browse"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    // ── Browse POST (search) ──────────────────────────────────────────────────

    @Test
    void searchPostReturnsResultsInBrowseView() throws Exception {
        FileData result = new FileData();
        result.setId(1L);
        result.setFileName("notes.pdf");
        when(storageService.findFilesByFilters(eq(1001L), any(), any(), any(), any()))
                .thenReturn(List.of(result));

        mockMvc.perform(post("/student/browse/search").with(csrf())
                        .param("departmentId", "1001")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .param("fileRole", "Notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/browse"))
                .andExpect(model().attributeExists("results"));
    }

    @Test
    void searchPostEnforcesDeptIdFromSessionNotForm() throws Exception {
        when(storageService.findFilesByFilters(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // submits departmentId=9999, but server should use student's own 1001
        mockMvc.perform(post("/student/browse/search").with(csrf())
                        .param("departmentId", "9999")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .param("fileRole", "Notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/browse"));

        verify(storageService).findFilesByFilters(eq(1001L), any(), any(), any(), any());
    }

    // ── Download: original ────────────────────────────────────────────────────

    @Test
    void downloadOriginalReturns404WhenFileIdNotFound() throws Exception {
        when(storageService.getFileById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/student/download/original/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadOriginalReturns404WhenFileNotOnDisk() throws Exception {
        FileData fd = new FileData();
        fd.setFileName("test.pptx");
        fd.setFilePath("/nonexistent/path");
        when(storageService.getFileById(1L)).thenReturn(Optional.of(fd));

        mockMvc.perform(get("/student/download/original/1"))
                .andExpect(status().isNotFound());
    }

    // ── Download: gzip ────────────────────────────────────────────────────────

    @Test
    void downloadGzipReturns404WhenFileIdNotFound() throws Exception {
        when(storageService.getFileById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/student/download/gzip/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadGzipReturns404WhenFileNotOnDisk() throws Exception {
        FileData fd = new FileData();
        fd.setFileName("test.pptx");
        fd.setFilePath("/nonexistent/path");
        when(storageService.getFileById(1L)).thenReturn(Optional.of(fd));

        mockMvc.perform(get("/student/download/gzip/1"))
                .andExpect(status().isNotFound());
    }

    // ── Download: zip ─────────────────────────────────────────────────────────

    @Test
    void downloadZipReturns404WhenFileIdNotFound() throws Exception {
        when(storageService.getFileById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/student/download/zip/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadZipReturns404WhenFileNotOnDisk() throws Exception {
        FileData fd = new FileData();
        fd.setFileName("test.pptx");
        fd.setFilePath("/nonexistent/path");
        when(storageService.getFileById(1L)).thenReturn(Optional.of(fd));

        mockMvc.perform(get("/student/download/zip/1"))
                .andExpect(status().isNotFound());
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    @Test
    void profilePageLoadsStudentData() throws Exception {
        mockMvc.perform(get("/student/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/profile"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    void saveProfileWithMatchingUserIdUpdatesEmailAndRedirects() throws Exception {
        when(userService.save(any(User.class))).thenReturn(studentUser);

        mockMvc.perform(post("/student/save-profile").with(csrf())
                        .param("userId", "student1")
                        .param("email", "new@test.edu")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(any(User.class));
    }

    @Test
    void saveProfileWithMismatchedUserIdDoesNotSaveAndRedirects() throws Exception {
        mockMvc.perform(post("/student/save-profile").with(csrf())
                        .param("userId", "other-student")
                        .param("email", "hacked@test.edu")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/profile"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void saveProfileWithPasswordMismatchRedirectsWithError() throws Exception {
        mockMvc.perform(post("/student/save-profile").with(csrf())
                        .param("userId", "student1")
                        .param("email", "student1@test.edu")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "different456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/profile"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void saveProfileWithMatchingPasswordsUpdatesPasswordAndRedirects() throws Exception {
        when(userService.save(any(User.class))).thenReturn(studentUser);

        mockMvc.perform(post("/student/save-profile").with(csrf())
                        .param("userId", "student1")
                        .param("email", "student1@test.edu")
                        .param("newPassword", "secure123")
                        .param("confirmPassword", "secure123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> u.getPassword().equals("{noop}secure123")));
    }
}
