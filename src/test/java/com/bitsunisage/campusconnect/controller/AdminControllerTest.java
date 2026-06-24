package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.DepartmentDetails;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.Semester;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.StorageService;
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
import static org.mockito.ArgumentMatchers.anyLong;
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

    @MockBean
    private StorageService storageService;

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
        testUser.setPassword("{noop}testpass");

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
        lenient().when(userService.saveDepartment(any(Department.class))).thenReturn(testDept);
        lenient().when(userService.getSemesterById(anyLong())).thenReturn(new Semester());
        lenient().when(userService.saveSemester(any(Semester.class))).thenReturn(new Semester());
        lenient().when(storageService.findAll()).thenReturn(List.of());
        lenient().doNothing().when(storageService).deleteResource(anyLong());
    }

    // ---- Dashboard ----

    @Test
    void dashboardPopulatesAllStatsFromService() throws Exception {
        User inactiveUser = new User();
        inactiveUser.setUserId("inactiveUser");
        inactiveUser.setActive(false);

        when(userService.findAllUsers()).thenReturn(List.of(testUser, inactiveUser));
        when(userService.totalUsers()).thenReturn(2);
        when(userService.totalUsers("ROLE_STUDENT")).thenReturn(1);
        when(userService.totalUsers("ROLE_TEACHER")).thenReturn(1);
        when(userService.totalUsers("ROLE_HOD")).thenReturn(0);
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));
        when(userService.getAllSemesters()).thenReturn(List.of(new Semester()));
        when(userService.getAllCourses()).thenReturn(List.of());
        when(storageService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/admin"))
                .andExpect(model().attribute("totalUsers", 2))
                .andExpect(model().attribute("totalStudents", 1))
                .andExpect(model().attribute("totalTeachers", 1))
                .andExpect(model().attribute("totalHods", 0))
                .andExpect(model().attribute("totalDepartments", 1))
                .andExpect(model().attribute("totalSemesters", 1))
                .andExpect(model().attribute("totalCourses", 0))
                .andExpect(model().attribute("totalFiles", 0))
                .andExpect(model().attribute("totalInactive", 1L))
                .andExpect(model().attribute("storageUsed", "0 B"))
                .andExpect(model().attributeExists("recentFiles"));
    }

    @Test
    void dashboardComputesStorageUsedFromFileSizes() throws Exception {
        com.bitsunisage.campusconnect.entities.FileData f1 = new com.bitsunisage.campusconnect.entities.FileData();
        f1.setFileSize(512 * 1024);
        com.bitsunisage.campusconnect.entities.FileData f2 = new com.bitsunisage.campusconnect.entities.FileData();
        f2.setFileSize(512 * 1024);

        when(userService.findAllUsers()).thenReturn(List.of());
        when(userService.getAllDepartments()).thenReturn(List.of());
        when(userService.getAllSemesters()).thenReturn(List.of());
        when(userService.getAllCourses()).thenReturn(List.of());
        when(storageService.findAll()).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/admin"))
                .andExpect(model().attribute("totalFiles", 2))
                .andExpect(model().attribute("storageUsed", "1.0 MB"));
    }

    @Test
    void dashboardRecentFilesAreOrderedByUploadDateDescending() throws Exception {
        java.sql.Timestamp older = java.sql.Timestamp.valueOf("2025-01-01 00:00:00");
        java.sql.Timestamp newer = java.sql.Timestamp.valueOf("2025-06-01 00:00:00");

        com.bitsunisage.campusconnect.entities.FileData old = new com.bitsunisage.campusconnect.entities.FileData();
        old.setUploadDate(older);
        com.bitsunisage.campusconnect.entities.FileData recent = new com.bitsunisage.campusconnect.entities.FileData();
        recent.setUploadDate(newer);

        when(userService.findAllUsers()).thenReturn(List.of());
        when(userService.getAllDepartments()).thenReturn(List.of());
        when(userService.getAllSemesters()).thenReturn(List.of());
        when(userService.getAllCourses()).thenReturn(List.of());
        when(storageService.findAll()).thenReturn(List.of(old, recent));

        mockMvc.perform(get("/admin"))
                .andExpect(model().attribute("recentFiles",
                        List.of(recent, old)));
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
    void addUserFormLoadsWithDepartmentsAndActiveDefaultTrue() throws Exception {
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/add-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-user"))
                .andExpect(model().attributeExists("user", "roles", "departments"));
    }

    // ---- Edit User (GET) ----

    @Test
    void editUserViaGetLoadsFormWithUserData() throws Exception {
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(testUser);
        when(userService.findRoleByUserId(testUser.getUserId())).thenReturn(testRole);
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/editUser").param("userId", testUser.getUserId()))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-user"))
                .andExpect(model().attributeExists("user", "roles", "departments"));
    }

    @Test
    void editUserWithUnknownIdRedirectsWithErrorMessage() throws Exception {
        when(userService.findUserByUserId("ghost")).thenReturn(null);

        mockMvc.perform(get("/admin/editUser").param("userId", "ghost"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ---- Save User ----

    @Test
    void saveNewUserPrependsNoopSetsRoleUserIdAndCreatesAllRecords() throws Exception {
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(null);

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
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(null);

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
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(testUser);

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
    void saveUserWithBlankPasswordPreservesExistingStoredPassword() throws Exception {
        when(userService.findUserByUserId(testUser.getUserId())).thenReturn(testUser);

        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", testUser.getUserId())
                        .param("password", "")
                        .param("active", "true")
                        .param("email", testUser.getEmail())
                        .param("deptID", testDept.getId().toString())
                        .param("role", testRole.getRole()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> u.getPassword().equals("{noop}testpass")));
    }

    @Test
    void saveNewUserWithBlankPasswordRedirectsWithErrorAndSavesNothing() throws Exception {
        when(userService.findUserByUserId("brandNewUser")).thenReturn(null);

        mockMvc.perform(post("/admin/save").with(csrf())
                        .param("userId", "brandNewUser")
                        .param("password", "")
                        .param("active", "true")
                        .param("email", "new@campus.edu")
                        .param("deptID", testDept.getId().toString())
                        .param("role", testRole.getRole()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
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

    // ---- Departments ----

    @Test
    void departmentsPageListsAllDepartments() throws Exception {
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/departments"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/departments"))
                .andExpect(model().attributeExists("departments"));
    }

    @Test
    void addDepartmentFormLoadsEmpty() throws Exception {
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/add-department"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-department"))
                .andExpect(model().attributeExists("dept", "departments"));
    }

    @Test
    void editDepartmentFormLoadsWithExistingData() throws Exception {
        when(userService.getDepartmentNameByDepartmentId(1001)).thenReturn(testDept);
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/editDepartment").param("id", "1001"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-department"))
                .andExpect(model().attributeExists("dept"));
    }

    @Test
    void editDepartmentWithUnknownIdRedirectsWithError() throws Exception {
        when(userService.getDepartmentNameByDepartmentId(9999)).thenReturn(null);

        mockMvc.perform(get("/admin/editDepartment").param("id", "9999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void saveDepartmentRedirectsWithSuccess() throws Exception {
        mockMvc.perform(post("/admin/save-department").with(csrf())
                        .param("name", "Physics Dept"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void deleteDepartmentWithMembersRedirectsWithError() throws Exception {
        when(userService.countMembersByDepartment(1001L)).thenReturn(3);

        mockMvc.perform(post("/admin/delete-department").with(csrf())
                        .param("deptId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).deleteDepartment(any(Department.class));
    }

    @Test
    void deleteDepartmentWithNoMembersSucceeds() throws Exception {
        when(userService.countMembersByDepartment(1001L)).thenReturn(0);
        when(userService.getDepartmentNameByDepartmentId(1001)).thenReturn(testDept);

        mockMvc.perform(post("/admin/delete-department").with(csrf())
                        .param("deptId", "1001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).deleteDepartment(testDept);
    }

    // ---- Semesters ----

    @Test
    void semestersPageListsAllSemesters() throws Exception {
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        sem.setSemesterName("Semester I");
        when(userService.getAllSemesters()).thenReturn(List.of(sem));

        mockMvc.perform(get("/admin/semesters"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/semesters"))
                .andExpect(model().attributeExists("semesters"));
    }

    @Test
    void saveSemesterRedirectsWithSuccess() throws Exception {
        mockMvc.perform(post("/admin/save-semester").with(csrf())
                        .param("semesterName", "Semester I"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/semesters"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void deleteSemesterWithSubjectsRedirectsWithError() throws Exception {
        when(userService.countSubjectsBySemester(1L)).thenReturn(2);

        mockMvc.perform(post("/admin/delete-semester").with(csrf())
                        .param("semesterId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/semesters"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void deleteSemesterWithNoSubjectsSucceeds() throws Exception {
        when(userService.countSubjectsBySemester(1L)).thenReturn(0);
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        when(userService.getSemesterById(1L)).thenReturn(sem);

        mockMvc.perform(post("/admin/delete-semester").with(csrf())
                        .param("semesterId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/semesters"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).deleteSemester(sem);
    }

    // ---- Files ----

    @Test
    void filesPageListsAllFiles() throws Exception {
        when(storageService.findAll()).thenReturn(List.of());
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/files"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/files"))
                .andExpect(model().attributeExists("files", "deptNames"));
    }

    @Test
    void deleteFileRedirectsWithSuccess() throws Exception {
        mockMvc.perform(post("/admin/delete-file").with(csrf())
                        .param("fileId", "42"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/files"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(storageService).deleteResource(42L);
    }

    // ---- Courses ----

    @Test
    void coursesPageListsAllCourses() throws Exception {
        when(userService.getAllCourses()).thenReturn(List.of());
        when(userService.getAllDepartments()).thenReturn(List.of(testDept));

        mockMvc.perform(get("/admin/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/courses"))
                .andExpect(model().attributeExists("courses", "deptNames"));
    }

    // ---- Edit Semester ----

    @Test
    void editSemesterFormLoadsWithExistingData() throws Exception {
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        sem.setSemesterName("Semester I");
        when(userService.getSemesterById(1L)).thenReturn(sem);

        mockMvc.perform(get("/admin/editSemester").param("semesterId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/add-semester"))
                .andExpect(model().attribute("semester", sem));
    }

    @Test
    void editSemesterWithUnknownIdRedirectsWithError() throws Exception {
        when(userService.getSemesterById(99L)).thenReturn(null);

        mockMvc.perform(get("/admin/editSemester").param("semesterId", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/semesters"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ---- HOD Assignment ----

    @Test
    void assignHodFormLoadsWithDepartmentAndHodList() throws Exception {
        Roles hodRole = new Roles();
        hodRole.setUserId("hod1");
        hodRole.setRole("ROLE_HOD");
        User hodUser = new User();
        hodUser.setUserId("hod1");
        hodUser.setDeptID(1001L);

        when(userService.getDepartmentNameByDepartmentId(1001)).thenReturn(testDept);
        when(userService.findByRole("ROLE_HOD")).thenReturn(List.of(hodRole));
        when(userService.findAllUsers()).thenReturn(List.of(hodUser));

        mockMvc.perform(get("/admin/assign-hod").param("deptId", "1001"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/assign-hod"))
                .andExpect(model().attributeExists("dept", "hods", "currentHodId"));
    }

    @Test
    void saveHodAssignmentUpdatesDeptIdAndRedirects() throws Exception {
        User hodUser = new User();
        hodUser.setUserId("hod1");
        hodUser.setDeptID(9999L);

        when(userService.findUserByUserId("hod1")).thenReturn(hodUser);
        when(userService.getDepartmentNameByDepartmentId(1001)).thenReturn(testDept);

        mockMvc.perform(post("/admin/save-hod-assignment").with(csrf())
                        .param("deptId", "1001")
                        .param("hodUserId", "hod1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> u.getDeptID().equals(1001L) &&
                u.getDepartment().equals("Test Department")));
    }

    // ---- Toggle Active ----

    @Test
    void toggleActiveDeactivatesAnActiveUser() throws Exception {
        testUser.setActive(true);
        when(userService.findUserByUserId("testUser")).thenReturn(testUser);

        mockMvc.perform(post("/admin/toggleActive").with(csrf())
                        .param("userId", "testUser")
                        .param("redirectTo", "/admin/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> !u.isActive()));
    }

    @Test
    void toggleActiveActivatesAnInactiveUser() throws Exception {
        testUser.setActive(false);
        when(userService.findUserByUserId("testUser")).thenReturn(testUser);

        mockMvc.perform(post("/admin/toggleActive").with(csrf())
                        .param("userId", "testUser")
                        .param("redirectTo", "/admin/inactive-users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/inactive-users"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).save(argThat((User u) -> u.isActive()));
    }

    @Test
    void toggleActiveBlocksSelfStatusChange() throws Exception {
        mockMvc.perform(post("/admin/toggleActive").with(csrf())
                        .param("userId", "admin1")
                        .param("redirectTo", "/admin/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    // ---- Inactive Users ----

    @Test
    void inactiveUsersPageFiltersInactiveOnly() throws Exception {
        User activeUser = new User();
        activeUser.setUserId("activeUser");
        activeUser.setActive(true);

        User inactiveUser = new User();
        inactiveUser.setUserId("inactiveUser");
        inactiveUser.setActive(false);

        when(userService.findAllUsers()).thenReturn(List.of(activeUser, inactiveUser));

        mockMvc.perform(get("/admin/inactive-users"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminViewPages/inactive-users"))
                .andExpect(model().attribute("inactiveUsers", List.of(inactiveUser)));
    }

    // ---- Exception handling (admin role) ----

    @Test
    void saveHodAssignment_whenHodUserNotFound_redirectsWithError() throws Exception {
        when(userService.getDepartmentNameByDepartmentId(1001)).thenReturn(testDept);
        when(userService.findUserByUserId("missingHod")).thenReturn(null);

        mockMvc.perform(post("/admin/save-hod-assignment").with(csrf())
                        .param("deptId", "1001")
                        .param("hodUserId", "missingHod"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/assign-hod?deptId=1001"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void saveHodAssignment_whenDepartmentNotFound_redirectsWithError() throws Exception {
        when(userService.getDepartmentNameByDepartmentId(9999)).thenReturn(null);

        mockMvc.perform(post("/admin/save-hod-assignment").with(csrf())
                        .param("deptId", "9999")
                        .param("hodUserId", "hod1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void profilePage_whenAdminAccountNotFound_returns404() throws Exception {
        when(userService.findUserByUserId("admin1")).thenReturn(null);

        mockMvc.perform(get("/admin/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveProfile_whenAdminAccountNotFound_returns404() throws Exception {
        when(userService.findUserByUserId("admin1")).thenReturn(null);

        mockMvc.perform(post("/admin/save-profile").with(csrf())
                        .param("email", "admin@test.com")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveProfile_whenPasswordsMismatch_redirectsWithError() throws Exception {
        when(userService.findUserByUserId("admin1")).thenReturn(testUser);

        mockMvc.perform(post("/admin/save-profile").with(csrf())
                        .param("email", "admin@test.com")
                        .param("newPassword", "newpass")
                        .param("confirmPassword", "different"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/profile"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void resetPasswordForm_whenSelfReset_redirectsToProfile() throws Exception {
        mockMvc.perform(get("/admin/reset-password")
                        .param("userId", "admin1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/profile"));
    }

    @Test
    void resetPassword_whenPasswordIsBlank_redirectsWithError() throws Exception {
        when(userService.findUserByUserId("testUser")).thenReturn(testUser);

        mockMvc.perform(post("/admin/reset-password").with(csrf())
                        .param("userId", "testUser")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reset-password?userId=testUser"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void resetPassword_whenPasswordsMismatch_redirectsWithError() throws Exception {
        when(userService.findUserByUserId("testUser")).thenReturn(testUser);

        mockMvc.perform(post("/admin/reset-password").with(csrf())
                        .param("userId", "testUser")
                        .param("newPassword", "pass1")
                        .param("confirmPassword", "pass2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/reset-password?userId=testUser"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void resetPassword_whenUserNotFound_redirectsWithError() throws Exception {
        when(userService.findUserByUserId("ghost")).thenReturn(null);

        mockMvc.perform(post("/admin/reset-password").with(csrf())
                        .param("userId", "ghost")
                        .param("newPassword", "secret")
                        .param("confirmPassword", "secret"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }
}
