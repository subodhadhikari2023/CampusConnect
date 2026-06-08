package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MasterController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class MasterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void landingPageReturnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void loginPageRedirectsToPriorUrlWhenSetInSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("url_prior_login", "/admin");

        mockMvc.perform(get("/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void loginPageWithNoSessionAttributeReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginFile"));
    }

    @Test
    void viewDataPopulatesModelAttributes() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(userService.findAllRoles()).thenReturn(Collections.emptyList());
        when(userService.getAllDepartments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/view-data"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("members", "roles", "departments"))
                .andExpect(view().name("ReferralData"));
    }
}
