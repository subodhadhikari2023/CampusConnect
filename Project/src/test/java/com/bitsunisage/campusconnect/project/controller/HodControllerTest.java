package com.bitsunisage.campusconnect.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HodController.class)
@WithMockUser(username = "hod1", roles = "HOD")
class HodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void hodHomeReturnsHodView() throws Exception {
        mockMvc.perform(get("/hod"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/hod"));
    }

    @Test
    void addCoursePageReturnsAddCourseView() throws Exception {
        mockMvc.perform(get("/hodViewPages/add-course"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/add-course"));
    }

    @Test
    void manageCoursePaseReturnsManageCourseView() throws Exception {
        mockMvc.perform(get("/hodViewPages/manage-course"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-course"));
    }
}
