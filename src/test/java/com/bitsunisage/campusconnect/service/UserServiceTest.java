package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.repository.*;
import com.bitsunisage.campusconnect.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserDAO userDAO;
    @Mock private RoleDAO roleDAO;
    @Mock private DepartmentDAO departmentDAO;
    @Mock private CourseDetailsDAO courseDetailsDAO;
    @Mock private SemesterDAO semesterDAO;
    @Mock private SubjectDetailsDAO subjectDetailsDAO;

    @InjectMocks
    private UserServiceImplementation userService;

    private User testUser;
    private Roles testRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("student1");
        testUser.setPassword("{noop}password");
        testUser.setActive(true);

        testRole = new Roles();
        testRole.setUserId("student1");
        testRole.setRole("ROLE_STUDENT");
    }

    @Test
    void findAllUsersReturnsAllUsers() {
        when(userDAO.findAll()).thenReturn(List.of(testUser));
        List<User> result = userService.findAllUsers();
        assertThat(result).hasSize(1).contains(testUser);
    }

    @Test
    void findAllRolesReturnsAllRoles() {
        when(roleDAO.findAll()).thenReturn(List.of(testRole));
        List<Roles> result = userService.findAllRoles();
        assertThat(result).hasSize(1).contains(testRole);
    }

    @Test
    void findUserByUserIdDelegatesToDAO() {
        when(userDAO.findByUserId("student1")).thenReturn(testUser);
        User result = userService.findUserByUserId("student1");
        assertThat(result).isEqualTo(testUser);
        verify(userDAO).findByUserId("student1");
    }

    @Test
    void findRoleByUserIdDelegatesToDAO() {
        when(roleDAO.findByUserId("student1")).thenReturn(testRole);
        Roles result = userService.findRoleByUserId("student1");
        assertThat(result).isEqualTo(testRole);
        verify(roleDAO).findByUserId("student1");
    }

    @Test
    void saveUserDelegatesToDAOAndReturns() {
        when(userDAO.save(testUser)).thenReturn(testUser);
        User result = userService.save(testUser);
        assertThat(result).isEqualTo(testUser);
        verify(userDAO).save(testUser);
    }

    @Test
    void saveRoleDelegatesToDAOAndReturns() {
        when(roleDAO.save(testRole)).thenReturn(testRole);
        Roles result = userService.save(testRole);
        assertThat(result).isEqualTo(testRole);
        verify(roleDAO).save(testRole);
    }

    @Test
    void deleteUserCallsDAODelete() {
        userService.deleteUser(testUser);
        verify(userDAO).delete(testUser);
    }

    @Test
    void deleteRoleCallsDAODelete() {
        userService.deleteRole(testRole);
        verify(roleDAO).delete(testRole);
    }

    @Test
    void totalUsersCountsAllUsers() {
        when(userDAO.findAll()).thenReturn(List.of(testUser, new User()));
        assertThat(userService.totalUsers()).isEqualTo(2);
    }

    @Test
    void totalUsersByRoleCountsCorrectly() {
        when(roleDAO.readRolesByRole("ROLE_STUDENT")).thenReturn(List.of(testRole));
        assertThat(userService.totalUsers("ROLE_STUDENT")).isEqualTo(1);
    }

    @Test
    void findByRoleReturnsMatchingRoles() {
        when(roleDAO.readRolesByRole("ROLE_STUDENT")).thenReturn(List.of(testRole));
        List<Roles> result = userService.findByRole("ROLE_STUDENT");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo("ROLE_STUDENT");
    }

    @Test
    void findByRoleReturnsEmptyListWhenNoMatch() {
        when(roleDAO.readRolesByRole("ROLE_HOD")).thenReturn(List.of());
        assertThat(userService.findByRole("ROLE_HOD")).isEmpty();
    }

    @Test
    void getAllDepartmentsReturnsAllDepartments() {
        Department dept = new Department();
        when(departmentDAO.findAll()).thenReturn(List.of(dept));
        assertThat(userService.getAllDepartments()).hasSize(1);
    }

    @Test
    void getAllCoursesReturnsAllCourses() {
        CourseDetails course = new CourseDetails();
        when(courseDetailsDAO.findAll()).thenReturn(List.of(course));
        assertThat(userService.getAllCourses()).hasSize(1);
    }

    @Test
    void getAllSemestersReturnsAllSemesters() {
        Semester semester = new Semester();
        when(semesterDAO.findAll()).thenReturn(List.of(semester));
        assertThat(userService.getAllSemesters()).hasSize(1);
    }

    @Test
    void getAllSubjectsReturnsAllSubjects() {
        SubjectDetails subject = new SubjectDetails();
        when(subjectDetailsDAO.findAll()).thenReturn(List.of(subject));
        assertThat(userService.getAllSubjects()).hasSize(1);
    }
}
