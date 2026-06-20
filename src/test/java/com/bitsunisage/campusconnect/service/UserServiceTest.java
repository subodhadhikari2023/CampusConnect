package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.repository.*;
import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.entities.DepartmentDetails;
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
    @Mock private DepartmentDetailsDAO departmentDetailsDAO;
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

    @Test
    void getCoursesByDepartmentIdDelegatesToDAO() {
        CourseDetails course = new CourseDetails();
        when(courseDetailsDAO.findByDepartmentId(1001L)).thenReturn(List.of(course));
        List<CourseDetails> result = userService.getCoursesByDepartmentId(1001L);
        assertThat(result).hasSize(1);
        verify(courseDetailsDAO).findByDepartmentId(1001L);
    }

    @Test
    void saveCourseDelegatesToDAOAndReturns() {
        CourseDetails course = new CourseDetails();
        course.setCourseName("BCA");
        when(courseDetailsDAO.save(course)).thenReturn(course);
        CourseDetails result = userService.saveCourse(course);
        assertThat(result.getCourseName()).isEqualTo("BCA");
        verify(courseDetailsDAO).save(course);
    }

    @Test
    void getCourseByIdDelegatesToDAO() {
        CourseDetails course = new CourseDetails();
        when(courseDetailsDAO.findByCourseId(1L)).thenReturn(course);
        assertThat(userService.getCourseById(1L)).isEqualTo(course);
        verify(courseDetailsDAO).findByCourseId(1L);
    }

    @Test
    void deleteCourseByIdDelegatesToDAO() {
        userService.deleteCourseById(1L);
        verify(courseDetailsDAO).deleteByCourseId(1L);
    }

    @Test
    void getSubjectsByCourseIdDelegatesToDAO() {
        SubjectDetails subject = new SubjectDetails();
        when(subjectDetailsDAO.findByCourseId(1)).thenReturn(List.of(subject));
        List<SubjectDetails> result = userService.getSubjectsByCourseId(1);
        assertThat(result).hasSize(1);
        verify(subjectDetailsDAO).findByCourseId(1);
    }

    @Test
    void saveSubjectDelegatesToDAOAndReturns() {
        SubjectDetails subject = new SubjectDetails();
        subject.setSubjectName("Data Structures");
        when(subjectDetailsDAO.save(subject)).thenReturn(subject);
        SubjectDetails result = userService.saveSubject(subject);
        assertThat(result.getSubjectName()).isEqualTo("Data Structures");
        verify(subjectDetailsDAO).save(subject);
    }

    @Test
    void deleteSubjectByIdDelegatesToDAO() {
        userService.deleteSubjectById(5L);
        verify(subjectDetailsDAO).deleteBySubjectId(5L);
    }

    @Test
    void countMembersByDepartmentAndRoleDelegatesToDAO() {
        when(departmentDetailsDAO.countByDepartmentIdAndRole(1001, "TEACHER")).thenReturn(3);
        int result = userService.countMembersByDepartmentAndRole(1001L, "TEACHER");
        assertThat(result).isEqualTo(3);
        verify(departmentDetailsDAO).countByDepartmentIdAndRole(1001, "TEACHER");
    }

    @Test
    void saveDepartmentDetailsDelegatesToDAO() {
        DepartmentDetails details = new DepartmentDetails();
        details.setUserName("teacher1");
        details.setDepartmentId(1001);
        details.setRole("TEACHER");
        when(departmentDetailsDAO.save(details)).thenReturn(details);
        DepartmentDetails result = userService.saveDepartmentDetails(details);
        assertThat(result.getUserName()).isEqualTo("teacher1");
        verify(departmentDetailsDAO).save(details);
    }

    @Test
    void deleteDepartmentDetailsByUserNameDelegatesToDAO() {
        userService.deleteDepartmentDetailsByUserName("teacher1");
        verify(departmentDetailsDAO).deleteByUserName("teacher1");
    }

    @Test
    void saveDepartmentDelegatesToDAO() {
        Department dept = new Department();
        dept.setName("Physics");
        when(departmentDAO.save(dept)).thenReturn(dept);
        Department result = userService.saveDepartment(dept);
        assertThat(result.getName()).isEqualTo("Physics");
        verify(departmentDAO).save(dept);
    }

    @Test
    void deleteDepartmentDelegatesToDAO() {
        Department dept = new Department();
        dept.setId(1L);
        userService.deleteDepartment(dept);
        verify(departmentDAO).delete(dept);
    }

    @Test
    void countMembersByDepartmentDelegatesToDAO() {
        when(departmentDetailsDAO.countByDepartmentId(1001)).thenReturn(5);
        int result = userService.countMembersByDepartment(1001L);
        assertThat(result).isEqualTo(5);
        verify(departmentDetailsDAO).countByDepartmentId(1001);
    }

    @Test
    void saveSemesterDelegatesToDAO() {
        Semester semester = new Semester();
        semester.setSemesterName("Semester I");
        when(semesterDAO.save(semester)).thenReturn(semester);
        Semester result = userService.saveSemester(semester);
        assertThat(result.getSemesterName()).isEqualTo("Semester I");
        verify(semesterDAO).save(semester);
    }

    @Test
    void deleteSemesterDelegatesToDAO() {
        Semester semester = new Semester();
        semester.setSemesterId(1L);
        userService.deleteSemester(semester);
        verify(semesterDAO).delete(semester);
    }

    @Test
    void getSemesterByIdDelegatesToDAO() {
        Semester semester = new Semester();
        semester.setSemesterId(2L);
        when(semesterDAO.findById(2)).thenReturn(java.util.Optional.of(semester));
        Semester result = userService.getSemesterById(2L);
        assertThat(result).isEqualTo(semester);
        verify(semesterDAO).findById(2);
    }

    @Test
    void countSubjectsBySemesterDelegatesToDAO() {
        when(subjectDetailsDAO.countBySemesterId(3)).thenReturn(7);
        int result = userService.countSubjectsBySemester(3L);
        assertThat(result).isEqualTo(7);
        verify(subjectDetailsDAO).countBySemesterId(3);
    }
}
