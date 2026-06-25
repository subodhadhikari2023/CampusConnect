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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link WebMvcTest} slice for {@link TeacherController}.
 * All tests run as teacher1 (ROLE_TEACHER).
 */
@WebMvcTest(TeacherController.class)
@Import(TestWebConfig.class)
@WithMockUser(username = "teacher1", roles = "TEACHER")
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StorageService storageService;

    private User teacherUser;
    private CourseDetails ownedCourse;
    private Semester ownedSemester;
    private SubjectDetails ownedSubject;
    private Department dept;

    @BeforeEach
    void setUp() {
        teacherUser = new User();
        teacherUser.setUserId("teacher1");
        teacherUser.setDeptID(1001L);
        teacherUser.setDepartment("Computer Applications");
        teacherUser.setEmail("teacher1@test.edu");

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

        lenient().when(userService.findUserByUserId("teacher1")).thenReturn(teacherUser);
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
    void teacherHomeReturnsTeacherView() throws Exception {
        mockMvc.perform(get("/teacher"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/indexteacher"));
    }

    @Test
    void teacherHomePopulatesAnnouncementsForTeachersDept() throws Exception {
        Announcement a = new Announcement();
        a.setTitle("Test Announcement");
        when(userService.getAnnouncementsByDeptId(1001L)).thenReturn(List.of(a));

        mockMvc.perform(get("/teacher"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("announcements"));
    }

    // ── Upload form GETs ──────────────────────────────────────────────────────

    @Test
    void uploadPPTsPageReturnsUploadPPTsView() throws Exception {
        mockMvc.perform(get("/teacher/uploadPPTs"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadPPTs"))
                .andExpect(model().attributeExists("fileUploadDTO", "courses",
                        "semesterList", "subjectList", "departmentName"));
    }

    @Test
    void uploadNotesPageReturnsUploadNotesView() throws Exception {
        mockMvc.perform(get("/teacher/uploadNotes"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadNotes"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void uploadSampleProgramsPageReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/teacher/uploadsampleprograms"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadsampleprograms"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void uploadAudioBooksPageReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/teacher/uploadaudiobooks"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadaudiobooks"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void uploadReferenceBooksPageReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/teacher/uploadReferenceBooks"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadReferenceBooks"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void uploadVideosPageReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/teacher/uploadVideos"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadVideos"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    // ── Upload POST validation ────────────────────────────────────────────────

    @Test
    void uploadWithEmptyFileRedirectsWithError() throws Exception {
        mockMvc.perform(multipart("/teacher/upload")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(storageService, never()).uploadToFileSystem(any());
    }

    @Test
    void uploadWithWrongDeptRedirectsWithError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pptx",
                "application/octet-stream", "data".getBytes());

        // departmentId 999 != teacher's 1001
        mockMvc.perform(multipart("/teacher/upload")
                        .file(file)
                        .param("departmentId", "999")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(storageService, never()).uploadToFileSystem(any());
    }

    @Test
    void uploadWithCourseFromWrongDeptRedirectsWithError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pptx",
                "application/octet-stream", "data".getBytes());

        CourseDetails wrongDeptCourse = new CourseDetails();
        wrongDeptCourse.setCourseId(2L);
        wrongDeptCourse.setDepartmentId(9999L);
        when(userService.getCourseById(2L)).thenReturn(wrongDeptCourse);

        mockMvc.perform(multipart("/teacher/upload")
                        .file(file)
                        .param("departmentId", "1001")
                        .param("courseId", "2")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .with(csrf()))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(storageService, never()).uploadToFileSystem(any());
    }

    @Test
    void uploadWithSemesterFromWrongCourseRedirectsWithError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pptx",
                "application/octet-stream", "data".getBytes());

        Semester wrongCourseSemester = new Semester();
        wrongCourseSemester.setSemesterId(1L);
        wrongCourseSemester.setCourseId(99L);

        when(userService.getCourseById(1L)).thenReturn(ownedCourse);
        when(userService.getSemesterById(1L)).thenReturn(wrongCourseSemester);

        mockMvc.perform(multipart("/teacher/upload")
                        .file(file)
                        .param("departmentId", "1001")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .with(csrf()))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(storageService, never()).uploadToFileSystem(any());
    }

    @Test
    void uploadWithSubjectFromWrongCourseRedirectsWithError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pptx",
                "application/octet-stream", "data".getBytes());

        SubjectDetails wrongCourseSubject = new SubjectDetails();
        wrongCourseSubject.setSubjectId(1L);
        wrongCourseSubject.setCourseId(99);

        when(userService.getCourseById(1L)).thenReturn(ownedCourse);
        when(userService.getSemesterById(1L)).thenReturn(ownedSemester);
        when(userService.getSubjectById(1L)).thenReturn(wrongCourseSubject);

        mockMvc.perform(multipart("/teacher/upload")
                        .file(file)
                        .param("departmentId", "1001")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .with(csrf()))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(storageService, never()).uploadToFileSystem(any());
    }

    @Test
    void uploadWithValidDataCallsStorageAndRedirectsWithSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pptx",
                "application/octet-stream", "data".getBytes());

        when(userService.getCourseById(1L)).thenReturn(ownedCourse);
        when(userService.getSemesterById(1L)).thenReturn(ownedSemester);
        when(userService.getSubjectById(1L)).thenReturn(ownedSubject);
        doNothing().when(storageService).uploadToFileSystem(any());

        mockMvc.perform(multipart("/teacher/upload")
                        .file(file)
                        .param("departmentId", "1001")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(storageService).uploadToFileSystem(any());
    }

    // ── View uploaded resources ───────────────────────────────────────────────

    @Test
    void viewUploadedResourcesReturnsResourcesView() throws Exception {
        when(storageService.findResourcesUploaded("teacher1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teacher/viewUploadedResources"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/viewUploadedResources"))
                .andExpect(model().attributeExists("user", "resources"));
    }

    // ── Delete resource ───────────────────────────────────────────────────────

    @Test
    void deleteResourceRedirectsToTeacherHome() throws Exception {
        doNothing().when(storageService).deleteResource(1L);

        mockMvc.perform(post("/teacher/deleteResource").with(csrf())
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"));

        verify(storageService).deleteResource(1L);
    }

    // ── Browse resources ──────────────────────────────────────────────────────

    @Test
    void browseResourcesPageReturnsBrowseView() throws Exception {
        mockMvc.perform(get("/teacher/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/browse"))
                .andExpect(model().attributeExists("fileUploadDTO", "courses",
                        "semesterList", "subjectList"));
    }

    @Test
    void searchResourcesPostReturnsResultsInBrowseView() throws Exception {
        FileData result = new FileData();
        result.setId(1L);
        result.setFileName("lecture1.pdf");
        when(storageService.findFilesByFilters(eq(1001L), any(), any(), any(), any()))
                .thenReturn(List.of(result));

        mockMvc.perform(post("/teacher/browse/search").with(csrf())
                        .param("departmentId", "1001")
                        .param("courseId", "1")
                        .param("semesterId", "1")
                        .param("subjectId", "1")
                        .param("fileRole", "Notes"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/browse"))
                .andExpect(model().attributeExists("results"));
    }

    // ── Download — original ───────────────────────────────────────────────────

    @Test
    void downloadOriginalReturns404WhenRecordNotFound() throws Exception {
        when(storageService.getFileById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teacher/download/original/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadOriginalReturns404WhenFileNotOnDisk() throws Exception {
        FileData fd = new FileData();
        fd.setFilePath("/nonexistent/path");
        fd.setFileName("missing.pdf");
        when(storageService.getFileById(1L)).thenReturn(Optional.of(fd));

        mockMvc.perform(get("/teacher/download/original/1"))
                .andExpect(status().isNotFound());
    }

    // ── Download — gzip ───────────────────────────────────────────────────────

    @Test
    void downloadGzipReturns404WhenRecordNotFound() throws Exception {
        when(storageService.getFileById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teacher/download/gzip/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadGzipReturns404WhenFileNotOnDisk() throws Exception {
        FileData fd = new FileData();
        fd.setFilePath("/nonexistent/path");
        fd.setFileName("missing.pdf");
        when(storageService.getFileById(1L)).thenReturn(Optional.of(fd));

        mockMvc.perform(get("/teacher/download/gzip/1"))
                .andExpect(status().isNotFound());
    }

    // ── Download — zip ────────────────────────────────────────────────────────

    @Test
    void downloadZipReturns404WhenRecordNotFound() throws Exception {
        when(storageService.getFileById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/teacher/download/zip/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void downloadZipReturns404WhenFileNotOnDisk() throws Exception {
        FileData fd = new FileData();
        fd.setFilePath("/nonexistent/path");
        fd.setFileName("missing.pdf");
        when(storageService.getFileById(1L)).thenReturn(Optional.of(fd));

        mockMvc.perform(get("/teacher/download/zip/1"))
                .andExpect(status().isNotFound());
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    @Test
    void profilePageLoadsTeacherData() throws Exception {
        mockMvc.perform(get("/teacher/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/profile"))
                .andExpect(model().attributeExists("teacher"));
    }

    @Test
    void saveProfileWithMatchingUserIdUpdatesEmailAndRedirects() throws Exception {
        when(userService.save(any(User.class))).thenReturn(teacherUser);

        mockMvc.perform(post("/teacher/save-profile").with(csrf())
                        .param("userId", "teacher1")
                        .param("email", "new@test.edu")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(any(User.class));
    }

    @Test
    void saveProfileWithMismatchedUserIdDoesNotSaveAndRedirects() throws Exception {
        mockMvc.perform(post("/teacher/save-profile").with(csrf())
                        .param("userId", "other-teacher")
                        .param("email", "hacked@test.edu")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/profile"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void saveProfileWithPasswordMismatchRedirectsWithError() throws Exception {
        mockMvc.perform(post("/teacher/save-profile").with(csrf())
                        .param("userId", "teacher1")
                        .param("email", "teacher1@test.edu")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "different456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/profile"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void saveProfileWithMatchingPasswordsUpdatesPasswordAndRedirects() throws Exception {
        when(userService.save(any(User.class))).thenReturn(teacherUser);

        mockMvc.perform(post("/teacher/save-profile").with(csrf())
                        .param("userId", "teacher1")
                        .param("email", "teacher1@test.edu")
                        .param("newPassword", "secure123")
                        .param("confirmPassword", "secure123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/profile"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> u.getPassword().equals("{noop}secure123")));
    }

    // ── My Subjects ───────────────────────────────────────────────────────────

    @Test
    void mySubjectsPageLoadsAssignedSubjects() throws Exception {
        TeacherSubject assignment = new TeacherSubject();
        assignment.setTeacherId("teacher1");
        assignment.setSubjectId(1L);

        when(userService.getAssignmentsByTeacherId("teacher1")).thenReturn(List.of(assignment));
        when(userService.getSubjectName(List.of(1L))).thenReturn(List.of(ownedSubject));
        when(userService.getCourseName(any())).thenReturn(List.of(ownedCourse));
        when(userService.getSemesterName(any())).thenReturn(List.of(ownedSemester));

        mockMvc.perform(get("/teacher/my-subjects"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/my-subjects"))
                .andExpect(model().attributeExists("subjects", "courseNames", "semesterNames"));
    }

    @Test
    void mySubjectsPageWithNoAssignmentsShowsEmptyList() throws Exception {
        when(userService.getAssignmentsByTeacherId("teacher1")).thenReturn(Collections.emptyList());
        when(userService.getSubjectName(any())).thenReturn(Collections.emptyList());
        when(userService.getCourseName(any())).thenReturn(Collections.emptyList());
        when(userService.getSemesterName(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teacher/my-subjects"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/my-subjects"));
    }
}
