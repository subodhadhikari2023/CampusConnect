package com.bitsunisage.campusconnect.project.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    // --- Unauthenticated access (custom entry point redirects to /NoUrlFound) ---

    @Test
    void unauthenticatedAccessToAdminRedirectsToNoUrlFound() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/NoUrlFound"));
    }

    @Test
    void unauthenticatedAccessToStudentRedirectsToNoUrlFound() throws Exception {
        mockMvc.perform(get("/student"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/NoUrlFound"));
    }

    @Test
    void unauthenticatedAccessToTeacherRedirectsToNoUrlFound() throws Exception {
        mockMvc.perform(get("/teacher"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/NoUrlFound"));
    }

    @Test
    void unauthenticatedAccessToHodRedirectsToNoUrlFound() throws Exception {
        mockMvc.perform(get("/hod"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/NoUrlFound"));
    }

    // --- Role isolation (custom access denied handler redirects to /access-denied) ---

    @Test
    void studentCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/admin").with(user("student1").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void studentCannotAccessTeacherEndpoint() throws Exception {
        mockMvc.perform(get("/teacher").with(user("student1").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void studentCannotAccessHodEndpoint() throws Exception {
        mockMvc.perform(get("/hod").with(user("student1").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void teacherCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/admin").with(user("teacher1").roles("TEACHER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void teacherCannotAccessStudentEndpoint() throws Exception {
        mockMvc.perform(get("/student").with(user("teacher1").roles("TEACHER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void hodCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/admin").with(user("hod1").roles("HOD")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void adminCannotAccessStudentEndpoint() throws Exception {
        mockMvc.perform(get("/student").with(user("admin1").roles("ADMIN")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    // --- Each role can access its own area ---

    @Test
    void adminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/admin").with(user("admin1").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void studentCanAccessStudentEndpoint() throws Exception {
        mockMvc.perform(get("/student").with(user("student1").roles("STUDENT")))
                .andExpect(status().isOk());
    }

    @Test
    void teacherCanAccessTeacherEndpoint() throws Exception {
        mockMvc.perform(get("/teacher").with(user("teacher1").roles("TEACHER")))
                .andExpect(status().isOk());
    }

    @Test
    void hodCanAccessHodEndpoint() throws Exception {
        mockMvc.perform(get("/hod").with(user("hod1").roles("HOD")))
                .andExpect(status().isOk());
    }

    // --- Public endpoints ---

    @Test
    void loginPageIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void landingPageIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    // --- Logout ---

    @Test
    void logoutRedirectsToLandingPage() throws Exception {
        mockMvc.perform(logout())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
