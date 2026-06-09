package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
@WithMockUser(username = "teacher1", roles = "TEACHER")
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private StorageService storageService;

    @Test
    void teacherHomeReturnsTeacherView() throws Exception {
        mockMvc.perform(get("/teacher"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/indexteacher"));
    }

    @Test
    void uploadPPTsPageReturnsUploadPPTsView() throws Exception {
        when(userService.getAllDepartments()).thenReturn(Collections.emptyList());
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(userService.getAllCourses()).thenReturn(Collections.emptyList());
        when(userService.getAllSemesters()).thenReturn(Collections.emptyList());
        when(userService.getAllSubjects()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teacher/uploadPPTs"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadPPTs"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void uploadNotesPageReturnsUploadNotesView() throws Exception {
        when(userService.getAllDepartments()).thenReturn(Collections.emptyList());
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(userService.getAllCourses()).thenReturn(Collections.emptyList());
        when(userService.getAllSemesters()).thenReturn(Collections.emptyList());
        when(userService.getAllSubjects()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teacher/uploadNotes"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/uploadNotes"))
                .andExpect(model().attributeExists("fileUploadDTO"));
    }

    @Test
    void viewUploadedResourcesReturnsResourcesView() throws Exception {
        User user = new User();
        user.setUserId("teacher1");

        when(storageService.getCurrentOwnersName()).thenReturn("teacher1");
        when(userService.findUserByUserId("teacher1")).thenReturn(user);
        when(storageService.findResourcesUploaded("teacher1")).thenReturn(Collections.emptyList());
        when(userService.getCourseName(any())).thenReturn(Collections.emptyList());
        when(userService.getSemesterName(any())).thenReturn(Collections.emptyList());
        when(userService.getSubjectName(any())).thenReturn(Collections.emptyList());
        when(userService.getDepartmentNames(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/teacher/viewUploadedResources"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherViewPages/viewUploadedResources"))
                .andExpect(model().attributeExists("user", "resources"));
    }

    @Test
    void deleteResourceRedirectsToTeacherHome() throws Exception {
        doNothing().when(storageService).deleteResource(1L);

        mockMvc.perform(post("/teacher/deleteResource").with(csrf())
                        .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"));

        verify(storageService).deleteResource(1L);
    }

    @Test
    void uploadFileRedirectsToTeacherHome() throws Exception {
        doNothing().when(storageService).uploadToFileSystem(any());

        mockMvc.perform(post("/teacher/upload").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"));
    }
}
