package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.CourseDetails;
import com.bitsunisage.campusconnect.entities.Semester;
import com.bitsunisage.campusconnect.entities.SubjectDetails;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HodController.class)
@WithMockUser(username = "hod1", roles = "HOD")
class HodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User hodUser;

    @BeforeEach
    void setUp() {
        hodUser = new User();
        hodUser.setUserId("hod1");
        hodUser.setDeptID(1001L);
        hodUser.setDepartment("Computer Applications");
        when(userService.findUserByUserId("hod1")).thenReturn(hodUser);
        when(userService.countMembersByDepartmentAndRole(1001L, "TEACHER")).thenReturn(2);
        when(userService.countMembersByDepartmentAndRole(1001L, "STUDENT")).thenReturn(5);
        when(userService.getCoursesByDepartmentId(1001L)).thenReturn(List.of());
        when(userService.getAllSemesters()).thenReturn(List.of());
    }

    @Test
    void hodHomePopulatesDashboardModel() throws Exception {
        mockMvc.perform(get("/hod"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/hod"))
                .andExpect(model().attribute("departmentName", "Computer Applications"))
                .andExpect(model().attribute("totalFaculty", 2))
                .andExpect(model().attribute("totalStudents", 5))
                .andExpect(model().attribute("totalCourses", 0));
    }

    @Test
    void addCoursePagePassesDepartmentToModel() throws Exception {
        mockMvc.perform(get("/hod/add-course"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/add-course"))
                .andExpect(model().attribute("department", "Computer Applications"));
    }

    @Test
    void saveCourseRedirectsToManageCourse() throws Exception {
        CourseDetails saved = new CourseDetails();
        saved.setCourseName("BCA");
        saved.setDepartmentId(1001L);
        when(userService.saveCourse(any())).thenReturn(saved);

        mockMvc.perform(post("/hod/save-course").with(csrf()).param("courseName", "BCA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-course"));
    }

    @Test
    void manageCoursePageReturnsCourseList() throws Exception {
        CourseDetails c = new CourseDetails();
        c.setCourseName("BCA");
        c.setDepartmentId(1001L);
        when(userService.getCoursesByDepartmentId(1001L)).thenReturn(List.of(c));

        mockMvc.perform(get("/hod/manage-course"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-course"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    void updateCourseRedirectsToManageCourse() throws Exception {
        CourseDetails existing = new CourseDetails();
        existing.setCourseName("Old Name");
        when(userService.getCourseById(1L)).thenReturn(existing);
        when(userService.saveCourse(any())).thenReturn(existing);

        mockMvc.perform(post("/hod/update-course").with(csrf())
                        .param("courseId", "1")
                        .param("courseName", "New Name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-course"));
    }

    @Test
    void deleteCourseRedirectsToManageCourse() throws Exception {
        doNothing().when(userService).deleteCourseById(1L);

        mockMvc.perform(post("/hod/delete-course").with(csrf()).param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-course"));
    }

    @Test
    void deleteCourseWithChildrenRedirectsWithError() throws Exception {
        doThrow(DataIntegrityViolationException.class).when(userService).deleteCourseById(1L);

        mockMvc.perform(post("/hod/delete-course").with(csrf()).param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-course?error=has-subjects"));
    }

    @Test
    void addSubjectPagePassesCoursesAndSemesters() throws Exception {
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        sem.setSemesterName("Semester I");
        when(userService.getAllSemesters()).thenReturn(List.of(sem));

        mockMvc.perform(get("/hod/add-subject"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/add-subject"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attributeExists("semesters"));
    }

    @Test
    void saveSubjectRedirectsToManageSubjectForCourse() throws Exception {
        when(userService.saveSubject(any())).thenReturn(new SubjectDetails());

        mockMvc.perform(post("/hod/save-subject").with(csrf())
                        .param("subjectName", "Data Structures")
                        .param("courseId", "1")
                        .param("semesterId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-subject?courseId=1"));
    }

    @Test
    void manageSubjectWithCourseIdPopulatesSubjects() throws Exception {
        when(userService.getSubjectsByCourseId(1)).thenReturn(List.of());

        mockMvc.perform(get("/hod/manage-subject").param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-subject"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attribute("selectedCourseId", 1));
    }

    @Test
    void manageSubjectWithoutCourseIdShowsSelectorOnly() throws Exception {
        mockMvc.perform(get("/hod/manage-subject"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-subject"))
                .andExpect(model().attributeDoesNotExist("subjects"));
    }

    @Test
    void deleteSubjectRedirectsBackToCourse() throws Exception {
        doNothing().when(userService).deleteSubjectById(5L);

        mockMvc.perform(post("/hod/delete-subject").with(csrf())
                        .param("subjectId", "5")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-subject?courseId=1"));
    }
}
