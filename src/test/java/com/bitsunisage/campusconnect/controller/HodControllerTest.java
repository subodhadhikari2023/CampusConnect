package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.config.TestWebConfig;
import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HodController.class)
@Import(TestWebConfig.class)
@WithMockUser(username = "hod1", roles = "HOD")
class HodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User hodUser;

    /** A CourseDetails owned by hod1's department, used for ownership checks. */
    private CourseDetails ownedCourse;

    @BeforeEach
    void setUp() {
        hodUser = new User();
        hodUser.setUserId("hod1");
        hodUser.setDeptID(1001L);
        hodUser.setDepartment("Computer Applications");

        // ownedCourse has departmentId matching hodUser.deptId so ownsCourse() returns true
        ownedCourse = new CourseDetails();
        ownedCourse.setCourseId(1L);
        ownedCourse.setDepartmentId(1001L);
        ownedCourse.setCourseName("BCA");

        lenient().when(userService.findUserByUserId("hod1")).thenReturn(hodUser);
        lenient().when(userService.countMembersByDepartmentAndRole(1001L, "TEACHER")).thenReturn(2);
        lenient().when(userService.countMembersByDepartmentAndRole(1001L, "STUDENT")).thenReturn(5);
        lenient().when(userService.getCoursesByDepartmentId(1001L)).thenReturn(List.of());
        // Default ownership: any getCourseById returns ownedCourse so ownsCourse() passes
        lenient().when(userService.getCourseById(anyLong())).thenReturn(ownedCourse);
    }

    // ── Dashboard ────────────────────────────────────────────────────────────

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

    // ── Courses ──────────────────────────────────────────────────────────────

    @Test
    void addCoursePagePassesDepartmentToModel() throws Exception {
        mockMvc.perform(get("/hod/add-course"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/add-course"))
                .andExpect(model().attribute("department", "Computer Applications"));
    }

    @Test
    void saveCourseRedirectsToSemestersForNewCourse() throws Exception {
        CourseDetails saved = new CourseDetails();
        saved.setCourseId(7L);
        saved.setCourseName("BCA");
        saved.setDepartmentId(1001L);
        when(userService.saveCourse(any())).thenReturn(saved);

        // saveCourse redirects to /hod/semesters so the HOD can define semesters next
        mockMvc.perform(post("/hod/save-course").with(csrf()).param("courseName", "BCA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/semesters?courseId=7"));
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
        when(userService.saveCourse(any())).thenReturn(ownedCourse);

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
    void deleteCourseWithChildrenRedirectsWithErrorFlash() throws Exception {
        doThrow(DataIntegrityViolationException.class).when(userService).deleteCourseById(1L);

        mockMvc.perform(post("/hod/delete-course").with(csrf()).param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-course"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ── Semesters ────────────────────────────────────────────────────────────

    @Test
    void semestersPageWithCourseIdReturnsSemesterList() throws Exception {
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        sem.setSemesterName("Semester I");
        when(userService.getSemestersByCourseId(1L)).thenReturn(List.of(sem));

        mockMvc.perform(get("/hod/semesters").param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-semester"))
                .andExpect(model().attribute("course", ownedCourse))
                .andExpect(model().attributeExists("semesters"));
    }

    @Test
    void semestersPageWithoutCourseIdShowsCourseSelector() throws Exception {
        mockMvc.perform(get("/hod/semesters"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-semester"))
                .andExpect(model().attributeDoesNotExist("course"));
    }

    // ── Curriculum ───────────────────────────────────────────────────────────

    @Test
    void curriculumPageLoadsCourseAndSubjectsBySemester() throws Exception {
        CourseDetails course = new CourseDetails();
        course.setCourseId(7L);
        course.setCourseName("BCA");
        course.setDepartmentId(1001L); // must match hodUser.deptId for ownsCourse() to pass

        Semester sem = new Semester();
        sem.setSemesterId(1L);
        sem.setSemesterName("Semester I");
        sem.setCourseId(7L);

        SubjectDetails sub = new SubjectDetails();
        sub.setSubjectId(10L);
        sub.setSubjectName("Data Structures");
        sub.setCourseId(7);
        sub.setSemesterId(1);

        when(userService.getCourseById(7L)).thenReturn(course);
        when(userService.getSemestersByCourseId(7L)).thenReturn(List.of(sem)); // controller calls this, not getAllSemesters
        when(userService.getSubjectsByCourseId(7)).thenReturn(List.of(sub));

        mockMvc.perform(get("/hod/curriculum").param("courseId", "7"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/curriculum"))
                .andExpect(model().attribute("course", course))
                .andExpect(model().attributeExists("semesters"))
                .andExpect(model().attributeExists("subjectsBySemester"));
    }

    @Test
    void curriculumPageWithoutCourseIdShowsCourseSelector() throws Exception {
        mockMvc.perform(get("/hod/curriculum"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/curriculum"))
                .andExpect(model().attributeDoesNotExist("course"));
    }

    // ── Subjects ─────────────────────────────────────────────────────────────

    @Test
    void addSubjectPageWithoutCourseIdShowsSelectorOnly() throws Exception {
        mockMvc.perform(get("/hod/add-subject"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/add-subject"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attributeDoesNotExist("semesters")); // semesters only load after course is selected
    }

    @Test
    void addSubjectPageWithCourseIdLoadsSemesters() throws Exception {
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        sem.setSemesterName("Semester I");
        when(userService.getSemestersByCourseId(1L)).thenReturn(List.of(sem));

        mockMvc.perform(get("/hod/add-subject").param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/add-subject"))
                .andExpect(model().attributeExists("courses", "selectedCourse", "semesters"));
    }

    @Test
    void saveSubjectRedirectsToCurriculum() throws Exception {
        when(userService.saveSubject(any())).thenReturn(new SubjectDetails());

        mockMvc.perform(post("/hod/save-subject").with(csrf())
                        .param("subjectName", "Data Structures")
                        .param("courseId", "1")
                        .param("semesterId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/curriculum?courseId=1"));
    }

    @Test
    void manageSubjectWithCourseIdPopulatesSubjectsAndSemesters() throws Exception {
        when(userService.getSubjectsByCourseId(1)).thenReturn(List.of());

        mockMvc.perform(get("/hod/manage-subject").param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/manage-subject"))
                .andExpect(model().attributeExists("subjects"))
                .andExpect(model().attributeExists("semesters"))
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
    void deleteSubjectWithDefaultSourceRedirectsBackToCurriculum() throws Exception {
        doNothing().when(userService).deleteSubjectById(5L);

        mockMvc.perform(post("/hod/delete-subject").with(csrf())
                        .param("subjectId", "5")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/curriculum?courseId=1"));
    }

    @Test
    void deleteSubjectFromManageSubjectSourceRedirectsBackToManageSubject() throws Exception {
        doNothing().when(userService).deleteSubjectById(5L);

        mockMvc.perform(post("/hod/delete-subject").with(csrf())
                        .param("subjectId", "5")
                        .param("courseId", "1")
                        .param("source", "manage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-subject?courseId=1"));
    }

    // ── Members ──────────────────────────────────────────────────────────────

    @Test
    void membersPagePopulatesFacultyAndStudents() throws Exception {
        User teacher = new User();
        teacher.setUserId("teacher1");
        teacher.setEmail("t@test.edu");

        when(userService.getMembersByDepartmentAndRole(1001L, "TEACHER")).thenReturn(List.of(teacher));
        when(userService.getMembersByDepartmentAndRole(1001L, "STUDENT")).thenReturn(List.of());

        mockMvc.perform(get("/hod/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/members"))
                .andExpect(model().attributeExists("faculty", "students"));
    }

    // ── Profile ──────────────────────────────────────────────────────────────

    @Test
    void profilePageLoadsHodData() throws Exception {
        mockMvc.perform(get("/hod/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/profile"))
                .andExpect(model().attributeExists("hod"));
    }

    @Test
    void saveProfileWithPasswordMismatchRedirectsWithError() throws Exception {
        mockMvc.perform(post("/hod/save-profile").with(csrf())
                        .param("email", "hod@test.edu")
                        .param("newPassword", "pass1")
                        .param("confirmPassword", "pass2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/profile"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).save(any(User.class));
    }

    @Test
    void saveProfileWithValidDataRedirectsWithSuccess() throws Exception {
        mockMvc.perform(post("/hod/save-profile").with(csrf())
                        .param("email", "newhod@test.edu")
                        .param("newPassword", "")
                        .param("confirmPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/profile"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    // ── Semesters (write) ─────────────────────────────────────────────────────

    @Test
    void saveSemesterWithBlankNameRedirectsWithError() throws Exception {
        mockMvc.perform(post("/hod/save-semester").with(csrf())
                        .param("courseId", "1")
                        .param("semesterName", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/semesters?courseId=1"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).saveSemester(any());
    }

    @Test
    void saveSemesterSuccessAddsNewSemesterAndRedirects() throws Exception {
        when(userService.saveSemester(any())).thenReturn(new Semester());

        mockMvc.perform(post("/hod/save-semester").with(csrf())
                        .param("courseId", "1")
                        .param("semesterName", "Semester V"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/semesters?courseId=1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).saveSemester(any(Semester.class));
    }

    @Test
    void deleteSemesterWithSubjectsAssignedRedirectsWithError() throws Exception {
        when(userService.countSubjectsBySemester(1L)).thenReturn(3);

        mockMvc.perform(post("/hod/delete-semester").with(csrf())
                        .param("semesterId", "1")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/semesters?courseId=1"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).deleteSemester(any());
    }

    @Test
    void deleteSemesterSuccessDeletesAndRedirects() throws Exception {
        Semester sem = new Semester();
        sem.setSemesterId(1L);
        when(userService.countSubjectsBySemester(1L)).thenReturn(0);
        when(userService.getSemesterById(1L)).thenReturn(sem);
        doNothing().when(userService).deleteSemester(any());

        mockMvc.perform(post("/hod/delete-semester").with(csrf())
                        .param("semesterId", "1")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/semesters?courseId=1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).deleteSemester(sem);
    }

    @Test
    void updateSemesterSuccessRenamesAndRedirects() throws Exception {
        Semester existing = new Semester();
        existing.setSemesterId(1L);
        existing.setSemesterName("Semester I");
        existing.setCourseId(1L);
        when(userService.getSemesterById(1L)).thenReturn(existing);
        when(userService.semesterNameExistsInCourse(anyLong(), anyString())).thenReturn(false);
        when(userService.saveSemester(any())).thenReturn(existing);

        mockMvc.perform(post("/hod/update-semester").with(csrf())
                        .param("semesterId", "1")
                        .param("semesterName", "Semester One")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/semesters?courseId=1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).saveSemester(existing);
    }

    // ── Subjects (write) ──────────────────────────────────────────────────────

    @Test
    void updateSubjectSuccessRedirectsToManageSubject() throws Exception {
        SubjectDetails sub = new SubjectDetails();
        sub.setSubjectId(1L);
        sub.setSubjectName("Old Name");
        sub.setCourseId(1);
        when(userService.getSubjectById(1L)).thenReturn(sub);
        when(userService.saveSubject(any())).thenReturn(sub);

        mockMvc.perform(post("/hod/update-subject").with(csrf())
                        .param("subjectId", "1")
                        .param("subjectName", "Data Structures")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/manage-subject?courseId=1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).saveSubject(any(SubjectDetails.class));
    }

    // ── Teacher-Subject Assignment ────────────────────────────────────────────

    @Test
    void assignTeachersPageWithoutCourseIdShowsCourseSelector() throws Exception {
        when(userService.getMembersByDepartmentAndRole(1001L, "TEACHER")).thenReturn(List.of());

        mockMvc.perform(get("/hod/assign-teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/assign-teachers"))
                .andExpect(model().attributeExists("courses", "teachers"))
                .andExpect(model().attributeDoesNotExist("course"));
    }

    @Test
    void assignTeachersPageWithCourseIdShowsSubjectsGroupedBySemester() throws Exception {
        SubjectDetails sub = new SubjectDetails();
        sub.setSubjectId(10L);
        sub.setCourseId(1);
        sub.setSemesterId(1);
        when(userService.getMembersByDepartmentAndRole(1001L, "TEACHER")).thenReturn(List.of());
        when(userService.getSubjectsByCourseId(1)).thenReturn(List.of(sub));
        when(userService.getAssignmentsBySubjectIds(any())).thenReturn(List.of());
        when(userService.getSemestersByCourseId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/hod/assign-teachers").param("courseId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/assign-teachers"))
                .andExpect(model().attributeExists("course", "subjectsBySemester", "assignmentsBySubjectId"));
    }

    @Test
    void assignTeacherWithTeacherIdSavesAssignmentAndRedirects() throws Exception {
        User teacher = new User();
        teacher.setUserId("teacher1");
        teacher.setDeptID(1001L);
        when(userService.findUserByUserId("teacher1")).thenReturn(teacher);
        doNothing().when(userService).upsertTeacherAssignment(anyString(), anyLong());

        mockMvc.perform(post("/hod/assign-teacher").with(csrf())
                        .param("subjectId", "10")
                        .param("teacherId", "teacher1")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/assign-teachers?courseId=1"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).upsertTeacherAssignment("teacher1", 10L);
    }

    @Test
    void assignTeacherWithBlankTeacherIdRemovesAssignmentAndRedirects() throws Exception {
        doNothing().when(userService).removeTeacherAssignmentBySubjectId(anyLong());

        mockMvc.perform(post("/hod/assign-teacher").with(csrf())
                        .param("subjectId", "10")
                        .param("courseId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/assign-teachers?courseId=1"));

        verify(userService).removeTeacherAssignmentBySubjectId(10L);
    }

    // ── Department Files ──────────────────────────────────────────────────────

    @Test
    void departmentFilesPagePopulatesFilesAndLookupMaps() throws Exception {
        FileData file = new FileData();
        file.setId(1L);
        file.setCourseId(1L);
        file.setSemesterId(1L);
        file.setSubjectId(1L);
        when(userService.getFilesByDepartmentId(1001L)).thenReturn(List.of(file));
        when(userService.getCourseName(any())).thenReturn(List.of());
        when(userService.getSemesterName(any())).thenReturn(List.of());
        when(userService.getSubjectName(any())).thenReturn(List.of());

        mockMvc.perform(get("/hod/department-files"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/department-files"))
                .andExpect(model().attributeExists("files", "courseNames", "semesterNames", "subjectNames"));
    }

    // ── Announcements ─────────────────────────────────────────────────────────

    @Test
    void announcementsPageListsDeptAnnouncements() throws Exception {
        Announcement a = new Announcement();
        a.setId(1L);
        a.setDeptId(1001L);
        a.setTitle("Welcome");
        when(userService.getAnnouncementsByDeptId(1001L)).thenReturn(List.of(a));

        mockMvc.perform(get("/hod/announcements"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/announcements"))
                .andExpect(model().attributeExists("announcements"));
    }

    @Test
    void editAnnouncementPageLoadsForOwnedAnnouncement() throws Exception {
        Announcement a = new Announcement();
        a.setId(1L);
        a.setDeptId(1001L);
        a.setTitle("Test");
        a.setBody("Body text");
        when(userService.getAnnouncementById(1L)).thenReturn(Optional.of(a));

        mockMvc.perform(get("/hod/edit-announcement").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/edit-announcement"))
                .andExpect(model().attributeExists("announcement"));
    }

    @Test
    void editAnnouncementForUnknownIdRedirectsWithError() throws Exception {
        when(userService.getAnnouncementById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/hod/edit-announcement").param("id", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/announcements"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void saveAnnouncementCreatesAndRedirectsWithSuccess() throws Exception {
        when(userService.saveAnnouncement(any())).thenReturn(new Announcement());

        mockMvc.perform(post("/hod/save-announcement").with(csrf())
                        .param("title", "Exam Schedule Released")
                        .param("body", "Please check the notice board."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/announcements"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).saveAnnouncement(any(Announcement.class));
    }

    @Test
    void deleteAnnouncementDeletesOwnedAnnouncementAndRedirects() throws Exception {
        Announcement a = new Announcement();
        a.setId(1L);
        a.setDeptId(1001L);
        when(userService.getAnnouncementById(1L)).thenReturn(Optional.of(a));
        doNothing().when(userService).deleteAnnouncementById(1L);

        mockMvc.perform(post("/hod/delete-announcement").with(csrf()).param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/announcements"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).deleteAnnouncementById(1L);
    }

    @Test
    void updateAnnouncementWithBlankFieldsRedirectsWithError() throws Exception {
        mockMvc.perform(post("/hod/update-announcement").with(csrf())
                        .param("id", "1")
                        .param("title", "")
                        .param("body", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/announcements"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(userService, never()).saveAnnouncement(any());
    }

    @Test
    void updateAnnouncementSuccessUpdatesAndRedirects() throws Exception {
        Announcement a = new Announcement();
        a.setId(1L);
        a.setDeptId(1001L);
        a.setTitle("Old Title");
        a.setBody("Old body");
        when(userService.getAnnouncementById(1L)).thenReturn(Optional.of(a));
        when(userService.saveAnnouncement(any())).thenReturn(a);

        mockMvc.perform(post("/hod/update-announcement").with(csrf())
                        .param("id", "1")
                        .param("title", "Updated Title")
                        .param("body", "Updated body text"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hod/announcements"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService).saveAnnouncement(any(Announcement.class));
    }

    // ── Workload ──────────────────────────────────────────────────────────────

    @Test
    void workloadPagePopulatesTeachersAndAssignments() throws Exception {
        User teacher = new User();
        teacher.setUserId("teacher1");
        teacher.setDeptID(1001L);
        TeacherSubject assignment = new TeacherSubject();
        assignment.setTeacherId("teacher1");
        assignment.setSubjectId(1L);
        when(userService.getMembersByDepartmentAndRole(1001L, "TEACHER")).thenReturn(List.of(teacher));
        when(userService.getAssignmentsByTeacherIds(any())).thenReturn(List.of(assignment));
        when(userService.getSubjectName(any())).thenReturn(List.of());

        mockMvc.perform(get("/hod/workload"))
                .andExpect(status().isOk())
                .andExpect(view().name("hodViewPages/workload"))
                .andExpect(model().attributeExists("teachers", "assignmentsByTeacher"));
    }
}
