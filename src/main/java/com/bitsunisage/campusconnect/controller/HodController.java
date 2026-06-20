package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.CourseDetails;
import com.bitsunisage.campusconnect.entities.SubjectDetails;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Handles HTTP requests for the Head of Department (HOD) role.
 * All routes are under {@code /hod/**}, enforced by Spring Security with {@code ROLE_HOD}.
 * The HOD can manage courses and subjects scoped to their own department.
 */
@Controller
public class HodController {

    private final UserService userService;

    /**
     * @param userService service for user, course, and subject operations
     */
    @Autowired
    public HodController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Renders the HOD dashboard populated with department statistics.
     *
     * @param model populated with departmentName, totalFaculty, totalStudents, totalCourses
     * @return Thymeleaf template {@code hodViewPages/hod}
     */
    @GetMapping("/hod")
    public String homePage(Model model) {
        User hod = getLoggedInUser();
        Long deptId = hod.getDeptID();
        model.addAttribute("departmentName", hod.getDepartment());
        model.addAttribute("totalFaculty", userService.countMembersByDepartmentAndRole(deptId, "TEACHER"));
        model.addAttribute("totalStudents", userService.countMembersByDepartmentAndRole(deptId, "STUDENT"));
        model.addAttribute("totalCourses", userService.getCoursesByDepartmentId(deptId).size());
        return "hodViewPages/hod";
    }

    /**
     * Renders the add-course form. The department is pre-set from the HOD's own department.
     *
     * @param model populated with the HOD's department name
     * @return Thymeleaf template {@code hodViewPages/add-course}
     */
    @GetMapping("/hod/add-course")
    public String showAddCoursePage(Model model) {
        model.addAttribute("department", getLoggedInUser().getDepartment());
        return "hodViewPages/add-course";
    }

    /**
     * Saves a new course bound to the HOD's department and redirects to manage-course.
     *
     * @param courseName name of the new course
     * @return redirect to {@code /hod/manage-course}
     */
    @PostMapping("/hod/save-course")
    public String saveCourse(@RequestParam("courseName") String courseName) {
        User hod = getLoggedInUser();
        CourseDetails course = new CourseDetails();
        course.setCourseName(courseName);
        course.setDepartmentId(hod.getDeptID());
        userService.saveCourse(course);
        return "redirect:/hod/manage-course";
    }

    /**
     * Renders the manage-courses page listing all courses for the HOD's department.
     *
     * @param model     populated with the department's course list
     * @param error     optional error key shown when a deletion could not be completed
     * @return Thymeleaf template {@code hodViewPages/manage-course}
     */
    @GetMapping("/hod/manage-course")
    public String showManageCoursePage(Model model,
                                       @RequestParam(value = "error", required = false) String error) {
        User hod = getLoggedInUser();
        List<CourseDetails> courses = userService.getCoursesByDepartmentId(hod.getDeptID());
        model.addAttribute("courses", courses);
        if ("has-subjects".equals(error)) {
            model.addAttribute("errorMessage", "Cannot delete a course that still has subjects. Remove all subjects first.");
        }
        return "hodViewPages/manage-course";
    }

    /**
     * Updates the name of an existing course and redirects to manage-course.
     *
     * @param courseId   primary key of the course to rename
     * @param courseName new course name
     * @return redirect to {@code /hod/manage-course}
     */
    @PostMapping("/hod/update-course")
    public String updateCourse(@RequestParam("courseId") Long courseId,
                               @RequestParam("courseName") String courseName) {
        CourseDetails course = userService.getCourseById(courseId);
        course.setCourseName(courseName);
        userService.saveCourse(course);
        return "redirect:/hod/manage-course";
    }

    /**
     * Deletes a course. If the course still has subjects attached, redirects with an error.
     *
     * @param courseId primary key of the course to delete
     * @return redirect to {@code /hod/manage-course}, with {@code ?error=has-subjects} on failure
     */
    @PostMapping("/hod/delete-course")
    public String deleteCourse(@RequestParam("courseId") Long courseId) {
        try {
            userService.deleteCourseById(courseId);
        } catch (DataIntegrityViolationException e) {
            return "redirect:/hod/manage-course?error=has-subjects";
        }
        return "redirect:/hod/manage-course";
    }

    /**
     * Renders the add-subject form with the HOD's courses and all available semesters.
     *
     * @param model populated with the HOD's course list and all semesters
     * @return Thymeleaf template {@code hodViewPages/add-subject}
     */
    @GetMapping("/hod/add-subject")
    public String showAddSubjectPage(Model model) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        model.addAttribute("semesters", userService.getAllSemesters());
        return "hodViewPages/add-subject";
    }

    /**
     * Saves a new subject and redirects to manage-subject for its course.
     *
     * @param subjectName name of the new subject
     * @param courseId    course this subject belongs to
     * @param semesterId  semester this subject is taught in
     * @return redirect to {@code /hod/manage-subject} filtered by the saved subject's course
     */
    @PostMapping("/hod/save-subject")
    public String saveSubject(@RequestParam("subjectName") String subjectName,
                              @RequestParam("courseId") int courseId,
                              @RequestParam("semesterId") int semesterId) {
        SubjectDetails subject = new SubjectDetails();
        subject.setSubjectName(subjectName);
        subject.setCourseId(courseId);
        subject.setSemesterId(semesterId);
        userService.saveSubject(subject);
        return "redirect:/hod/manage-subject?courseId=" + courseId;
    }

    /**
     * Renders the manage-subjects page. When {@code courseId} is provided, the subject list
     * for that course is shown alongside the course selector.
     *
     * @param courseId optional course filter; when absent only the selector is shown
     * @param model    populated with courses, and optionally subjects and selectedCourseId
     * @return Thymeleaf template {@code hodViewPages/manage-subject}
     */
    @GetMapping("/hod/manage-subject")
    public String showManageSubjectPage(@RequestParam(value = "courseId", required = false) Integer courseId,
                                        Model model) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        if (courseId != null) {
            model.addAttribute("subjects", userService.getSubjectsByCourseId(courseId));
            model.addAttribute("selectedCourseId", courseId);
        }
        return "hodViewPages/manage-subject";
    }

    /**
     * Deletes a subject and redirects back to manage-subject for the same course.
     *
     * @param subjectId primary key of the subject to delete
     * @param courseId  the course to return to after deletion
     * @return redirect to {@code /hod/manage-subject?courseId=<courseId>}
     */
    @PostMapping("/hod/delete-subject")
    public String deleteSubject(@RequestParam("subjectId") Long subjectId,
                                @RequestParam("courseId") int courseId) {
        userService.deleteSubjectById(subjectId);
        return "redirect:/hod/manage-subject?courseId=" + courseId;
    }

    /**
     * Returns the {@link User} entity for the currently authenticated principal.
     *
     * @return the logged-in HOD's user record
     */
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUserId(username);
    }
}
