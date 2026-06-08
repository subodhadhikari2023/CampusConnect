package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.service.StorageService;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@WithMockUser(username = "student1", roles = "STUDENT")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StorageService storageService;

    private void stubFormModel() {
        when(userService.getAllDepartments()).thenReturn(Collections.emptyList());
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(userService.getAllCourses()).thenReturn(Collections.emptyList());
        when(userService.getAllSemesters()).thenReturn(Collections.emptyList());
        when(userService.getAllSubjects()).thenReturn(Collections.emptyList());
    }

    @Test
    void studentHomeReturnsStudentView() throws Exception {
        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentViewPages/indexstudent"));
    }

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
}
