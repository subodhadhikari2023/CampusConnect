package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.DepartmentDetails;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private User testUser;
    private Roles testRole;
    private Department testDept;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("testUser");
        testUser.setEmail("test@campus.edu");
        testUser.setActive(true);
        testUser.setDeptID(1001L);
        testUser.setDepartment("Test Department");

        testRole = new Roles();
        testRole.setUserId("testUser");
        testRole.setRole("ROLE_STUDENT");

        testDept = new Department();
        testDept.setId(1001L);
        testDept.setName("Test Department");

        lenient().when(userService.getDepartmentNameByDepartmentId(testDept.getId().intValue())).thenReturn(testDept);
        lenient().when(userService.save(any(User.class))).thenReturn(testUser);
        lenient().when(userService.save(any(Roles.class))).thenReturn(testRole);
        lenient().when(userService.saveDepartmentDetails(any(DepartmentDetails.class))).thenReturn(new DepartmentDetails());
    }

    // ---- Dashboard ----

    @Test
    void dashboardPopulatesAllStatsFromService() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(testUser));
        when(userService.findAllRoles()).thenReturn(List.of(testRole));
        when(userService.totalUsers()).thenReturn(5);
        when(userService.totalUsers("ROLE_STUDENT")).thenReturn(2);
        when(userService.totalUsers("ROLE_TEACHER")).thenReturn(2);
        when(userService.totalUsers("ROLE_HOD")).thenReturn(1);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/admin"))
                .andExpect(model().attribute("totalUsers", 5))
                .andExpect(model().attribute("totalStudents", 2))
                .andExpect(model().attribute("totalTeachers", 2))
                .andExpect(model().attribute("totalHods", 1));
    }

    // ---- User list pages (N+1 fix) ----

    @Test
    void studentsPageResolvesUsersWithoutPerUserQuery() throws Exception {
        when(userService.findByRole("ROLE_STUDENT")).thenReturn(List.of(testRole));
        when(userService.findAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/admin/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/getstudents"))
                .andExpect(model().attributeExists("userStudents"));

        verify(userService, never()).findUserByUserId(anyString());
    }

    @Test
    void teachersPageResolvesUsersWithoutPerUserQuery() throws Exception {
        Roles teacherRole = new Roles();
        teacherRole.setUserId(testUser.getUserId());
        teacherRole.setRole("ROLE_TEACHER");
        when(userService.findByRole("ROLE_TEACHER")).thenReturn(List.of(teacherRole));
        when(userService.findAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/admin/teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/getteachers"))
                .andExpect(model().attributeExists("userTeachers"));

        verify(userService, never()).findUserByUserId(anyString());
    }

    @Test
    void hodsPageResolvesUsersWithoutPerUserQuery() throws Exception {
        Roles hodRole = new Roles();
        hodRole.setUserId(testUser.getUserId());
        hodRole.setRole("ROLE_HOD");
        when(userService.findByRole("ROLE_HOD")).thenReturn(List.of(hodRole));
        when(userService.findAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/admin/hods"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/gethod"))
                .andExpect(model().attributeExists("userHODs"));

        verify(userService, never()).findUserByUserId(anyString());
    }

    // ---- Add User Form ----

    @Test
    void addUserFormLoadsWithDepartments() throws Exception {
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/add-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-user"))
                .andExpect(model().attributeExists("user", "roles", "departments"));
    }

    // ---- Save User ----

    @Test
    void saveNewUserPrependsNoopSetsRoleUserIdAndCreatesAllRecords() throws Exception {
        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", testUser.getUserId())
                        .param("password", "plainpassword")
                        .param("active", "true")
                        .param("email", testUser.getEmail())
                        .param("deptID", testDept.getId().toString())
                        .param("role", testRole.getRole()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> u.getPassword().startsWith("{noop}")));
        verify(userService).save(argThat((Roles r) -> r.getUserId().equals(testUser.getUserId())));
        verify(userService).saveDepartmentDetails(any(DepartmentDetails.class));
    }

    @Test
    void saveUserStripsRolePrefixBeforeStoringInDepartmentDetails() throws Exception {
        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", testUser.getUserId())
                        .param("password", "plainpassword")
                        .param("active", "true")
                        .param("email", testUser.getEmail())
                        .param("deptID", testDept.getId().toString())
                        .param("role", "ROLE_STUDENT"))
                .andExpect(status().is3xxRedirection());

        verify(userService).saveDepartmentDetails(argThat(d -> d.getRole().equals("STUDENT")));
    }

    @Test
    void saveExistingUserWithNoopPasswordDoesNotDoubleWrap() throws Exception {
        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", testUser.getUserId())
                        .param("password", "{noop}existingpassword")
                        .param("active", "true")
                        .param("email", testUser.getEmail())
                        .param("deptID", testDept.getId().toString())
                        .param("role", testRole.getRole()))
                .andExpect(status().is3xxRedirection());

        verify(userService).save(argThat((User u) -> u.getPassword().equals("{noop}existingpassword")));
    }

    @Test
    void saveUserWithMissingDeptIdRedirectsWithErrorAndSavesNothing() throws Exception {
        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", testUser.getUserId())
                        .param("password", "plainpassword")
                        .param("active", "true")
                        .param("email", testUser.getEmail())
                        .param("role", testRole.getRole()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    // ---- Delete User ----

    @Test
    void deleteUserFollowsFkOrderAndAddsSuccessMessage() throws Exception {
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(testUser);
        when(userService.findRoleByUserId(testUser.getUserId())).thenReturn(testRole);

        mockMvc.perform(post("/admin/deleteUser").with(csrf())
                        .param("userId", testUser.getUserId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("successMessage"));

        InOrder order = inOrder(userService);
        order.verify(userService).deleteDepartmentDetailsByUserName(testUser.getUserId());
        order.verify(userService).deleteRole(testRole);
        order.verify(userService).deleteUser(testUser);
    }

    @Test
    void deleteOwnAccountIsBlockedWithErrorMessage() throws Exception {
        mockMvc.perform(post("/admin/deleteUser").with(csrf())
                        .param("userId", "admin1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).deleteDepartmentDetailsByUserName(anyString());
        verify(userService, never()).deleteUser(any());
    }

    // ---- Update/Delete Form Selection ----

    @Test
    void showUpdateFormPassesActionAndUserListToModel() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/admin/showuserUpdateForm").param("action", "update"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/showUserFormForUpdate"))
                .andExpect(model().attribute("action", "update"))
                .andExpect(model().attributeExists("userList"));
    }

    @Test
    void showDeleteFormPassesActionAndUserListToModel() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/admin/showuserUpdateForm").param("action", "delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/showUserFormForUpdate"))
                .andExpect(model().attribute("action", "delete"))
                .andExpect(model().attributeExists("userList"));
    }

    // ---- Load User for Edit ----

    @Test
    void loadUserPopulatesFormWithExistingUserData() throws Exception {
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(testUser);
        when(userService.findRoleByUserId(testUser.getUserId())).thenReturn(testRole);
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(post("/admin/loadUser").with(csrf())
                        .param("userId", testUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-user"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("roles", testRole));
    }
}
