package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.config.TestWebConfig;
import com.bitsunisage.campusconnect.entities.Announcement;
import com.bitsunisage.campusconnect.entities.FileData;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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

    @TempDir
    Path tempDir;

    private User studentUser;

    @BeforeEach
    void setUp() {
        studentUser = new User();
        studentUser.setUserId("student1");
        studentUser.setDeptID(1001L);
        studentUser.setDepartment("Computer Applications");
        studentUser.setEmail("student1@test.edu");

        lenient().when(userService.findUserByUserId("student1")).thenReturn(studentUser);
    }

    private void stubFormModel() {
        when(userService.getAllDepartments()).thenReturn(Collections.emptyList());
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(userService.getAllCourses()).thenReturn(Collections.emptyList());
        when(userService.getAllSemesters()).thenReturn(Collections.emptyList());
        when(userService.getAllSubjects()).thenReturn(Collections.emptyList());
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

    // ── Search form GETs ──────────────────────────────────────────────────────

    @Test
    void pptsPageReturnsSearchView() throws Exception {
        stubFormModel();
        mockMvc.perform(get("/student/PPTs"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/searchppts"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void notesPageReturnsSearchView() throws Exception {
        stubFormModel();
        mockMvc.perform(get("/student/notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/searchppts"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void programsPageReturnsSearchView() throws Exception {
        stubFormModel();
        mockMvc.perform(get("/student/programs"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/searchppts"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void audiobooksPageReturnsSearchView() throws Exception {
        stubFormModel();
        mockMvc.perform(get("/student/audiobooks"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/searchppts"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void referenceBooksPageReturnsSearchView() throws Exception {
        stubFormModel();
        mockMvc.perform(get("/student/referencebooks"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/searchppts"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void videoPageReturnsSearchView() throws Exception {
        stubFormModel();
        mockMvc.perform(get("/student/video"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/searchppts"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    // ── fetchData POST ────────────────────────────────────────────────────────

    @Test
    void fetchDataReturnsPPTsView() throws Exception {
        stubFormModel();
        when(storageService.findFilesByFilters(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(userService.getCourseName(any())).thenReturn(Collections.emptyList());
        when(userService.getSemesterName(any())).thenReturn(Collections.emptyList());
        when(userService.getSubjectName(any())).thenReturn(Collections.emptyList());
        when(userService.getDepartmentNames(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/student/PPTs/fetchData").with(csrf())
                        .param("departmentId", "1001")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .param("fileRole", "PPTs"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/PPTs"))
                .andExpect(model().attributeExists("fileDataList"));
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
                        .param("email", "new@test.edu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(any(User.class));
    }

    @Test
    void saveProfileWithMismatchedUserIdDoesNotSaveAndRedirects() throws Exception {
        mockMvc.perform(post("/student/save-profile").with(csrf())
                        .param("userId", "other-student")
                        .param("email", "hacked@test.edu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/profile"));

        verify(userService, never()).save(any(User.class));
    }
}
