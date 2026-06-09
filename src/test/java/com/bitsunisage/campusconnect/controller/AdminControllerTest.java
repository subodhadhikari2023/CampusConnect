package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@WithMockUser(username = "admin1", roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void adminHomeReturnsCorrectViewWithModelAttributes() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());
        when(userService.findAllRoles()).thenReturn(Collections.emptyList());
        when(userService.totalUsers()).thenReturn(0);
        when(userService.totalUsers("ROLE_STUDENT")).thenReturn(0);
        when(userService.totalUsers("ROLE_TEACHER")).thenReturn(0);
        when(userService.totalUsers("ROLE_HOD")).thenReturn(0);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/admin"))
                .andExpect(model().attributeExists("users", "roles", "totalUsers",
                        "totalStudents", "totalTeachers", "totalHods"));
    }

    @Test
    void getStudentsReturnsStudentListView() throws Exception {
        Roles role = new Roles();
        role.setUserId("student1");
        role.setRole("ROLE_STUDENT");
        User user = new User();
        user.setUserId("student1");

        when(userService.findByRole("ROLE_STUDENT")).thenReturn(List.of(role));
        when(userService.findUserByUserId("student1")).thenReturn(user);

        mockMvc.perform(get("/admin/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/getstudents"))
                .andExpect(model().attributeExists("userStudents"));
    }

    @Test
    void getTeachersReturnsTeacherListView() throws Exception {
        when(userService.findByRole("ROLE_TEACHER")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/getteachers"))
                .andExpect(model().attributeExists("userTeachers"));
    }

    @Test
    void getHodsReturnsHodListView() throws Exception {
        when(userService.findByRole("ROLE_HOD")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/hods"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/gethod"))
                .andExpect(model().attributeExists("userHODs"));
    }

    @Test
    void addUserFormReturnsFormView() throws Exception {
        when(userService.getAllDepartments()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/add-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-user"))
                .andExpect(model().attributeExists("user", "roles", "departments"));
    }

    @Test
    void saveUserPrependsNopoPrefixAndRedirects() throws Exception {
        User savedUser = new User();
        Roles savedRole = new Roles();
        when(userService.save(any(User.class))).thenReturn(savedUser);
        when(userService.save(any(Roles.class))).thenReturn(savedRole);

        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", "newuser1")
                        .param("password", "secret")
                        .param("active", "true")
                        .param("email", "newuser1@campus.com")
                        .param("department", "Computer Science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService).save(argThat((User u) -> u.getPassword().startsWith("{noop}")));
    }

    @Test
    void deleteUserRemovesRoleAndUserThenRedirects() throws Exception {
        User user = new User();
        user.setUserId("student1");
        Roles roles = new Roles();
        roles.setUserId("student1");

        when(userService.findUserByUserId("student1")).thenReturn(user);
        when(userService.findRoleByUserId("student1")).thenReturn(roles);

        mockMvc.perform(post("/admin/deleteUser").with(csrf())
                        .param("userId", "student1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(userService).deleteRole(roles);
        verify(userService).deleteUser(user);
    }

    @Test
    void showUserUpdateFormReturnsUpdateView() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/showuserUpdateForm").param("action", "update"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/showUserFormForUpdate"))
                .andExpect(model().attributeExists("userList", "action"));
    }

    @Test
    void loadUserPopulatesFormForEditing() throws Exception {
        User user = new User();
        user.setUserId("student1");
        Roles roles = new Roles();
        roles.setUserId("student1");
        Department dept = new Department();

        when(userService.findUserByUserId("student1")).thenReturn(user);
        when(userService.findRoleByUserId("student1")).thenReturn(roles);
        when(userService.getAllDepartments()).thenReturn(List.of(dept));

        mockMvc.perform(post("/admin/loadUser").with(csrf())
                        .param("userId", "student1"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-user"))
                .andExpect(model().attributeExists("user", "roles", "departments"));
    }
}
