package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.*;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles HTTP requests for the Head of Department (HOD) role.
 * All routes are under {@code /hod/**}, enforced by Spring Security with {@code ROLE_HOD}.
 * The HOD can manage courses and subjects scoped to their own department, and edit their profile.
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

    // ── Dashboard ────────────────────────────────────────────────────────────

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
        model.addAttribute("totalFaculty",   userService.countMembersByDepartmentAndRole(deptId, "TEACHER"));
        model.addAttribute("totalStudents",  userService.countMembersByDepartmentAndRole(deptId, "STUDENT"));
        model.addAttribute("totalCourses",   userService.getCoursesByDepartmentId(deptId).size());
        model.addAttribute("totalSubjects",  userService.countSubjectsByDepartmentId(deptId));
        model.addAttribute("totalFiles",     userService.getFilesByDepartmentId(deptId).size());
        model.addAttribute("announcementCount", userService.getAnnouncementsByDeptId(deptId).size());
        return "hodViewPages/hod";
    }

    // ── Profile ──────────────────────────────────────────────────────────────

    /**
     * Renders the HOD's own profile page for editing their email and password.
     * Clears the stored password so it is never exposed in the form.
     *
     * @param model populated with {@code hod} — the current HOD's {@link User} record
     * @return Thymeleaf template {@code hodViewPages/profile}
     */
    @GetMapping("/hod/profile")
    public String profilePage(Model model) {
        User hod = getLoggedInUser();
        hod.setPassword("");
        model.addAttribute("hod", hod);
        return "hodViewPages/profile";
    }

    /**
     * Saves the HOD's updated email and optional new password.
     * A blank new-password field preserves the existing password.
     *
     * @param email           new email address
     * @param newPassword     new password; blank means keep current
     * @param confirmPassword must match {@code newPassword} when provided
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/profile}
     */
    @PostMapping("/hod/save-profile")
    public String saveProfile(@RequestParam("email") String email,
                              @RequestParam("newPassword") String newPassword,
                              @RequestParam("confirmPassword") String confirmPassword,
                              RedirectAttributes redirectAttributes) {
        if (!newPassword.isBlank() && !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/hod/profile";
        }
        User hod = getLoggedInUser();
        hod.setEmail(email);
        if (!newPassword.isBlank()) {
            hod.setPassword("{noop}" + newPassword);
        }
        userService.save(hod);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/hod/profile";
    }

    // ── Courses ──────────────────────────────────────────────────────────────

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
     * Saves a new course bound to the HOD's department and redirects to its curriculum page.
     *
     * @param courseName         name of the new course
     * @param redirectAttributes used to pass error flash messages on failure
     * @return redirect to {@code /hod/semesters?courseId=<id>} so the HOD can define semesters next
     */
    @PostMapping("/hod/save-course")
    public String saveCourse(@RequestParam("courseName") String courseName,
                             RedirectAttributes redirectAttributes) {
        if (courseName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course name cannot be blank.");
            return "redirect:/hod/add-course";
        }
        User hod = getLoggedInUser();
        if (userService.courseNameExistsInDepartment(hod.getDeptID(), courseName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "A course named \"" + courseName + "\" already exists in your department.");
            return "redirect:/hod/add-course";
        }
        CourseDetails course = new CourseDetails();
        course.setCourseName(courseName);
        course.setDepartmentId(hod.getDeptID());
        CourseDetails saved = userService.saveCourse(course);
        redirectAttributes.addFlashAttribute("successMessage",
                "Course created. Now define the semesters for it.");
        return "redirect:/hod/semesters?courseId=" + saved.getCourseId();
    }

    /**
     * Renders the manage-courses page listing all courses for the HOD's department.
     *
     * @param model populated with the department's course list and any flash messages
     * @return Thymeleaf template {@code hodViewPages/manage-course}
     */
    @GetMapping("/hod/manage-course")
    public String showManageCoursePage(Model model) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        return "hodViewPages/manage-course";
    }

    /**
     * Updates the name of an existing course after verifying it belongs to the HOD's department.
     *
     * @param courseId           primary key of the course to rename
     * @param courseName         new course name
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/manage-course}
     */
    @PostMapping("/hod/update-course")
    public String updateCourse(@RequestParam("courseId") Long courseId,
                               @RequestParam("courseName") String courseName,
                               RedirectAttributes redirectAttributes) {
        if (courseName == null || courseName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Course name cannot be blank.");
            return "redirect:/hod/manage-course";
        }
        User hod = getLoggedInUser();
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-course";
        }
        CourseDetails course = userService.getCourseById(courseId);
        course.setCourseName(courseName);
        userService.saveCourse(course);
        redirectAttributes.addFlashAttribute("successMessage", "Course renamed successfully.");
        return "redirect:/hod/manage-course";
    }

    /**
     * Deletes a course after verifying ownership. Redirects with an error if subjects still exist.
     *
     * @param courseId           primary key of the course to delete
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/manage-course}
     */
    @PostMapping("/hod/delete-course")
    public String deleteCourse(@RequestParam("courseId") Long courseId,
                               RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-course";
        }
        try {
            userService.deleteCourseById(courseId);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete a course that still has subjects. Remove all subjects first.");
        }
        return "redirect:/hod/manage-course";
    }

    // ── Semesters ────────────────────────────────────────────────────────────

    /**
     * Renders the semester management page for a single course.
     * When {@code courseId} is absent a course picker is shown.
     *
     * @param courseId optional course to manage semesters for
     * @param model    populated with {@code courses}, and optionally {@code course} and {@code semesters}
     * @return Thymeleaf template {@code hodViewPages/manage-semester}
     */
    @GetMapping("/hod/semesters")
    public String manageSemesters(@RequestParam(value = "courseId", required = false) Long courseId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        if (courseId != null) {
            if (!ownsCourse(courseId, hod.getDeptID())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
                return "redirect:/hod/manage-course";
            }
            model.addAttribute("course", userService.getCourseById(courseId));
            model.addAttribute("semesters", userService.getSemestersByCourseId(courseId));
        }
        return "hodViewPages/manage-semester";
    }

    /**
     * Creates a new semester for the given course.
     *
     * @param courseId           the course this semester belongs to
     * @param semesterName       display name (e.g. "Semester I")
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/semesters?courseId=<courseId>}
     */
    @PostMapping("/hod/save-semester")
    public String saveSemester(@RequestParam("courseId") Long courseId,
                               @RequestParam("semesterName") String semesterName,
                               RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied.");
            return "redirect:/hod/manage-course";
        }
        if (semesterName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Semester name cannot be blank.");
            return "redirect:/hod/semesters?courseId=" + courseId;
        }
        Semester semester = new Semester();
        semester.setSemesterName(semesterName);
        semester.setCourseId(courseId);
        userService.saveSemester(semester);
        redirectAttributes.addFlashAttribute("successMessage", "Semester '" + semesterName + "' added.");
        return "redirect:/hod/semesters?courseId=" + courseId;
    }

    /**
     * Deletes a semester after verifying it has no subjects assigned.
     *
     * @param semesterId         the semester to delete
     * @param courseId           used for ownership check and redirect
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/semesters?courseId=<courseId>}
     */
    @PostMapping("/hod/delete-semester")
    public String deleteSemester(@RequestParam("semesterId") Long semesterId,
                                 @RequestParam("courseId") Long courseId,
                                 RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied.");
            return "redirect:/hod/manage-course";
        }
        if (userService.countSubjectsBySemester(semesterId) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete a semester that has subjects assigned. Remove all subjects first.");
            return "redirect:/hod/semesters?courseId=" + courseId;
        }
        Semester semester = userService.getSemesterById(semesterId);
        userService.deleteSemester(semester);
        redirectAttributes.addFlashAttribute("successMessage", "Semester deleted.");
        return "redirect:/hod/semesters?courseId=" + courseId;
    }

    /**
     * Renames a semester after verifying its course belongs to the HOD's department.
     *
     * @param semesterId         primary key of the semester to rename
     * @param semesterName       new name; must not be blank or already used in this course
     * @param courseId           used for ownership check and redirect
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/semesters?courseId=<courseId>}
     */
    @PostMapping("/hod/update-semester")
    public String updateSemester(@RequestParam("semesterId") Long semesterId,
                                 @RequestParam("semesterName") String semesterName,
                                 @RequestParam("courseId") Long courseId,
                                 RedirectAttributes redirectAttributes) {
        if (semesterName == null || semesterName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Semester name cannot be blank.");
            return "redirect:/hod/semesters?courseId=" + courseId;
        }
        User hod = getLoggedInUser();
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-course";
        }
        Semester existing = userService.getSemesterById(semesterId);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Semester not found.");
            return "redirect:/hod/semesters?courseId=" + courseId;
        }
        if (!existing.getSemesterName().equals(semesterName)
                && userService.semesterNameExistsInCourse(courseId, semesterName)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "A semester named \"" + semesterName + "\" already exists in this course.");
            return "redirect:/hod/semesters?courseId=" + courseId;
        }
        existing.setSemesterName(semesterName);
        userService.saveSemester(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Semester renamed successfully.");
        return "redirect:/hod/semesters?courseId=" + courseId;
    }

    // ── Curriculum ───────────────────────────────────────────────────────────

    /**
     * Renders the curriculum management page for a single course, showing all semesters
     * and their subjects with inline forms to add or remove subjects.
     * When {@code courseId} is absent the page shows a course picker instead.
     *
     * @param courseId optional course ID; when absent only the course selector is shown
     * @param model    populated with {@code courses}, and optionally {@code course},
     *                 {@code semesters}, {@code subjectsBySemester}, and {@code assignedTeachers}
     * @return Thymeleaf template {@code hodViewPages/curriculum}, or redirect on ownership failure
     */
    @GetMapping("/hod/curriculum")
    public String showCurriculumPage(@RequestParam(value = "courseId", required = false) Long courseId,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        if (courseId == null) {
            return "hodViewPages/curriculum";
        }
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-course";
        }
        List<SubjectDetails> subjects = userService.getSubjectsByCourseId(courseId.intValue());
        Map<Long, List<SubjectDetails>> grouped = subjects.stream()
                .collect(Collectors.groupingBy(s -> (long) s.getSemesterId(),
                         LinkedHashMap::new, Collectors.toList()));
        List<Long> subjectIds = subjects.stream().map(SubjectDetails::getSubjectId).toList();
        Map<Long, String> assignedTeachers = userService.getAssignmentsBySubjectIds(subjectIds).stream()
                .collect(Collectors.toMap(TeacherSubject::getSubjectId, TeacherSubject::getTeacherId));
        model.addAttribute("course", userService.getCourseById(courseId));
        model.addAttribute("semesters", userService.getSemestersByCourseId(courseId));
        model.addAttribute("subjectsBySemester", grouped);
        model.addAttribute("assignedTeachers", assignedTeachers);
        return "hodViewPages/curriculum";
    }

    // ── Subjects ─────────────────────────────────────────────────────────────

    /**
     * Renders the add-subject form. When {@code courseId} is provided the semesters for
     * that course are loaded; otherwise only the course selector is shown.
     *
     * @param courseId           optional course pre-selection; triggers semester load when present
     * @param model              populated with courses, and optionally selectedCourse + semesters
     * @return Thymeleaf template {@code hodViewPages/add-subject}
     */
    @GetMapping("/hod/add-subject")
    public String showAddSubjectPage(@RequestParam(value = "courseId", required = false) Long courseId,
                                     Model model) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        if (courseId != null) {
            model.addAttribute("selectedCourse", userService.getCourseById(courseId));
            model.addAttribute("semesters", userService.getSemestersByCourseId(courseId));
        }
        return "hodViewPages/add-subject";
    }

    /**
     * Saves a new subject after verifying the target course belongs to the HOD's department.
     * Redirects back to the course curriculum page on success.
     *
     * @param subjectName        name of the new subject
     * @param courseId           course this subject belongs to
     * @param semesterId         semester this subject is taught in
     * @param redirectAttributes used to pass error flash messages
     * @return redirect to {@code /hod/curriculum?courseId=<courseId>}
     */
    @PostMapping("/hod/save-subject")
    public String saveSubject(@RequestParam("subjectName") String subjectName,
                              @RequestParam("courseId") int courseId,
                              @RequestParam("semesterId") int semesterId,
                              RedirectAttributes redirectAttributes) {
        if (subjectName == null || subjectName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject name cannot be blank.");
            return "redirect:/hod/curriculum?courseId=" + courseId;
        }
        User hod = getLoggedInUser();
        if (!ownsCourse((long) courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-subject";
        }
        if (userService.subjectNameExistsInSlot(courseId, semesterId, subjectName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "A subject named \"" + subjectName + "\" already exists in this semester.");
            return "redirect:/hod/curriculum?courseId=" + courseId;
        }
        SubjectDetails subject = new SubjectDetails();
        subject.setSubjectName(subjectName);
        subject.setCourseId(courseId);
        subject.setSemesterId(semesterId);
        userService.saveSubject(subject);
        return "redirect:/hod/curriculum?courseId=" + courseId;
    }

    /**
     * Renders the manage-subjects page. When {@code courseId} is provided, the subject list
     * for that course is shown alongside the course selector, grouped by semester.
     *
     * @param courseId optional course filter; when absent only the selector is shown
     * @param model    populated with courses, semesters, and optionally subjects and selectedCourseId
     * @return Thymeleaf template {@code hodViewPages/manage-subject}
     */
    @GetMapping("/hod/manage-subject")
    public String showManageSubjectPage(@RequestParam(value = "courseId", required = false) Integer courseId,
                                        Model model) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        if (courseId != null) {
            List<SubjectDetails> subjects = userService.getSubjectsByCourseId(courseId);
            Map<Long, List<SubjectDetails>> grouped = subjects.stream()
                    .collect(Collectors.groupingBy(s -> (long) s.getSemesterId(),
                             LinkedHashMap::new, Collectors.toList()));
            model.addAttribute("subjects", subjects);
            model.addAttribute("subjectsBySemester", grouped);
            model.addAttribute("selectedCourseId", courseId);
            model.addAttribute("semesters", userService.getSemestersByCourseId((long) courseId));
        }
        return "hodViewPages/manage-subject";
    }

    /**
     * Renames a subject after verifying it belongs to a course in the HOD's department.
     *
     * @param subjectId          primary key of the subject to rename
     * @param subjectName        new subject name
     * @param courseId           the course this subject belongs to (used for redirect and ownership check)
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/manage-subject?courseId=<courseId>}
     */
    @PostMapping("/hod/update-subject")
    public String updateSubject(@RequestParam("subjectId") Long subjectId,
                                @RequestParam("subjectName") String subjectName,
                                @RequestParam("courseId") int courseId,
                                RedirectAttributes redirectAttributes) {
        if (subjectName == null || subjectName.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject name cannot be blank.");
            return "redirect:/hod/manage-subject?courseId=" + courseId;
        }
        User hod = getLoggedInUser();
        if (!ownsCourse((long) courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-subject";
        }
        SubjectDetails subject = userService.getSubjectById(subjectId);
        if (subject == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Subject not found.");
            return "redirect:/hod/manage-subject?courseId=" + courseId;
        }
        subject.setSubjectName(subjectName);
        userService.saveSubject(subject);
        redirectAttributes.addFlashAttribute("successMessage", "Subject renamed successfully.");
        return "redirect:/hod/manage-subject?courseId=" + courseId;
    }

    /**
     * Deletes a subject after verifying it belongs to a course in the HOD's department.
     * The {@code source} parameter controls where the user lands after deletion:
     * {@code "manage"} returns to the manage-subjects page; anything else returns to curriculum.
     *
     * @param subjectId          primary key of the subject to delete
     * @param courseId           used for redirect and ownership check
     * @param source             optional; {@code "manage"} to redirect to manage-subjects instead of curriculum
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to manage-subject or curriculum, depending on {@code source}
     */
    @PostMapping("/hod/delete-subject")
    public String deleteSubject(@RequestParam("subjectId") Long subjectId,
                                @RequestParam("courseId") int courseId,
                                @RequestParam(value = "source", defaultValue = "curriculum") String source,
                                RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        if (!ownsCourse((long) courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-subject";
        }
        userService.deleteSubjectById(subjectId);
        redirectAttributes.addFlashAttribute("successMessage", "Subject removed successfully.");
        if ("manage".equals(source)) {
            return "redirect:/hod/manage-subject?courseId=" + courseId;
        }
        return "redirect:/hod/curriculum?courseId=" + courseId;
    }

    // ── Teacher-Subject Assignment ───────────────────────────────────────────

    /**
     * Renders the teacher-assignment page. When {@code courseId} is provided, shows all subjects
     * for that course grouped by semester, each with their current teacher assignment and
     * a dropdown to change it. When absent, only the course picker is shown.
     *
     * @param courseId optional course filter; when absent only the selector is shown
     * @param model    populated with courses, teachers, semesters, subjectsBySemester,
     *                 assignmentsBySubjectId, and optionally course and selectedCourseId
     * @return Thymeleaf template {@code hodViewPages/assign-teachers}
     */
    @GetMapping("/hod/assign-teachers")
    public String showAssignTeachersPage(@RequestParam(value = "courseId", required = false) Long courseId,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        model.addAttribute("courses", userService.getCoursesByDepartmentId(hod.getDeptID()));
        model.addAttribute("teachers", userService.getMembersByDepartmentAndRole(hod.getDeptID(), "TEACHER"));
        if (courseId == null) {
            return "hodViewPages/assign-teachers";
        }
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-course";
        }
        List<SubjectDetails> subjects = userService.getSubjectsByCourseId(courseId.intValue());
        Map<Long, List<SubjectDetails>> grouped = subjects.stream()
                .collect(Collectors.groupingBy(s -> (long) s.getSemesterId(),
                         LinkedHashMap::new, Collectors.toList()));
        List<Long> subjectIds = subjects.stream().map(SubjectDetails::getSubjectId).toList();
        Map<Long, TeacherSubject> assignmentsBySubjectId = userService.getAssignmentsBySubjectIds(subjectIds)
                .stream().collect(Collectors.toMap(TeacherSubject::getSubjectId, a -> a));
        model.addAttribute("course", userService.getCourseById(courseId));
        model.addAttribute("semesters", userService.getSemestersByCourseId(courseId));
        model.addAttribute("subjectsBySemester", grouped);
        model.addAttribute("assignmentsBySubjectId", assignmentsBySubjectId);
        model.addAttribute("selectedCourseId", courseId);
        return "hodViewPages/assign-teachers";
    }

    /**
     * Saves or removes a teacher-subject assignment.
     * When {@code teacherId} is blank or absent, the existing assignment for the subject is removed.
     * Otherwise the assignment is created or replaced.
     *
     * @param subjectId          primary key of the subject being assigned
     * @param teacherId          login username of the teacher to assign; blank means unassign
     * @param courseId           used for redirect and ownership check
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/assign-teachers?courseId=<courseId>}
     */
    @PostMapping("/hod/assign-teacher")
    public String assignTeacher(@RequestParam("subjectId") Long subjectId,
                                @RequestParam(value = "teacherId", required = false) String teacherId,
                                @RequestParam("courseId") Long courseId,
                                RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        if (!ownsCourse(courseId, hod.getDeptID())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: course does not belong to your department.");
            return "redirect:/hod/manage-course";
        }
        if (teacherId == null || teacherId.isBlank()) {
            userService.removeTeacherAssignmentBySubjectId(subjectId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher assignment removed.");
        } else {
            User teacher = userService.findUserByUserId(teacherId);
            if (teacher == null || !teacher.getDeptID().equals(hod.getDeptID())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Teacher not found or not in your department.");
                return "redirect:/hod/assign-teachers?courseId=" + courseId;
            }
            userService.upsertTeacherAssignment(teacherId, subjectId);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher assigned successfully.");
        }
        return "redirect:/hod/assign-teachers?courseId=" + courseId;
    }

    // ── Department Files ─────────────────────────────────────────────────────

    /**
     * Renders the department file overview page showing all uploaded files
     * in the HOD's department, with resolved course, semester, and subject names.
     *
     * @param model populated with {@code files}, {@code courseNames}, {@code semesterNames},
     *              and {@code subjectNames} lookup maps
     * @return Thymeleaf template {@code hodViewPages/department-files}
     */
    @GetMapping("/hod/department-files")
    public String showDepartmentFilesPage(Model model) {
        User hod = getLoggedInUser();
        List<FileData> files = userService.getFilesByDepartmentId(hod.getDeptID());

        List<Long> courseIds   = files.stream().map(FileData::getCourseId).distinct().toList();
        List<Long> semIds      = files.stream().map(FileData::getSemesterId).distinct().toList();
        List<Long> subjectIds  = files.stream().map(FileData::getSubjectId).distinct().toList();

        Map<Long, String> courseNames = userService.getCourseName(courseIds).stream()
                .collect(Collectors.toMap(CourseDetails::getCourseId, CourseDetails::getCourseName));
        Map<Long, String> semesterNames = userService.getSemesterName(semIds).stream()
                .collect(Collectors.toMap(Semester::getSemesterId, Semester::getSemesterName));
        Map<Long, String> subjectNames = userService.getSubjectName(subjectIds).stream()
                .collect(Collectors.toMap(SubjectDetails::getSubjectId, SubjectDetails::getSubjectName));

        model.addAttribute("files", files);
        model.addAttribute("courseNames", courseNames);
        model.addAttribute("semesterNames", semesterNames);
        model.addAttribute("subjectNames", subjectNames);
        return "hodViewPages/department-files";
    }

    // ── Department Members ───────────────────────────────────────────────────

    /**
     * Renders the department members view showing all teachers and students
     * assigned to the HOD's department.
     *
     * @param model populated with {@code faculty} and {@code students} lists
     * @return Thymeleaf template {@code hodViewPages/members}
     */
    @GetMapping("/hod/members")
    public String showMembersPage(Model model) {
        User hod = getLoggedInUser();
        Long deptId = hod.getDeptID();
        model.addAttribute("faculty",  userService.getMembersByDepartmentAndRole(deptId, "TEACHER"));
        model.addAttribute("students", userService.getMembersByDepartmentAndRole(deptId, "STUDENT"));
        return "hodViewPages/members";
    }

    // ── Announcements ────────────────────────────────────────────────────────

    /**
     * Renders the announcements management page, listing all announcements for the HOD's
     * department and providing a form to create a new one.
     *
     * @param model populated with {@code announcements} list
     * @return Thymeleaf template {@code hodViewPages/announcements}
     */
    @GetMapping("/hod/announcements")
    public String showAnnouncementsPage(Model model) {
        User hod = getLoggedInUser();
        model.addAttribute("announcements", userService.getAnnouncementsByDeptId(hod.getDeptID()));
        return "hodViewPages/announcements";
    }

    /**
     * Saves a new announcement authored by the logged-in HOD.
     *
     * @param title              announcement headline; must not be blank
     * @param body               announcement body text; must not be blank
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/announcements}
     */
    @PostMapping("/hod/save-announcement")
    public String saveAnnouncement(@RequestParam("title") String title,
                                   @RequestParam("body") String body,
                                   RedirectAttributes redirectAttributes) {
        if (title.isBlank() || body.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Title and body are required.");
            return "redirect:/hod/announcements";
        }
        User hod = getLoggedInUser();
        Announcement a = new Announcement();
        a.setDeptId(hod.getDeptID());
        a.setAuthor(hod.getUserId());
        a.setTitle(title.strip());
        a.setBody(body.strip());
        userService.saveAnnouncement(a);
        redirectAttributes.addFlashAttribute("successMessage", "Announcement posted.");
        return "redirect:/hod/announcements";
    }

    /**
     * Deletes an announcement after verifying it belongs to the HOD's department.
     *
     * @param id                 primary key of the announcement to delete
     * @param redirectAttributes used to pass success/error flash messages
     * @return redirect to {@code /hod/announcements}
     */
    @PostMapping("/hod/delete-announcement")
    public String deleteAnnouncement(@RequestParam("id") Long id,
                                     RedirectAttributes redirectAttributes) {
        User hod = getLoggedInUser();
        userService.getAnnouncementById(id).ifPresent(a -> {
            if (a.getDeptId().equals(hod.getDeptID())) {
                userService.deleteAnnouncementById(id);
            }
        });
        redirectAttributes.addFlashAttribute("successMessage", "Announcement deleted.");
        return "redirect:/hod/announcements";
    }

    /**
     * Updates the title and body of an existing announcement.
     * Only the HOD who owns the department the announcement belongs to may edit it.
     *
     * @param id                 primary key of the announcement to update
     * @param title              new headline; must not be blank
     * @param body               new body text; must not be blank
     * @param redirectAttributes flash messages for the next request
     * @return redirect to {@code /hod/announcements}
     */
    @PostMapping("/hod/update-announcement")
    public String updateAnnouncement(@RequestParam("id") Long id,
                                     @RequestParam("title") String title,
                                     @RequestParam("body") String body,
                                     RedirectAttributes redirectAttributes) {
        if (title == null || title.isBlank() || body == null || body.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Title and message cannot be blank.");
            return "redirect:/hod/announcements";
        }
        User hod = getLoggedInUser();
        userService.getAnnouncementById(id).ifPresent(a -> {
            if (a.getDeptId().equals(hod.getDeptID())) {
                a.setTitle(title.trim());
                a.setBody(body.trim());
                userService.saveAnnouncement(a);
            }
        });
        redirectAttributes.addFlashAttribute("successMessage", "Announcement updated.");
        return "redirect:/hod/announcements";
    }

    // ── Teacher Workload ─────────────────────────────────────────────────────

    /**
     * Renders the teacher workload page showing each teacher in the HOD's department
     * alongside the subjects they are currently assigned to teach.
     *
     * @param model populated with {@code teachers}, {@code assignmentsByTeacher} (Map&lt;String,
     *              List&lt;SubjectDetails&gt;&gt;), and {@code subjectIndex} (Map&lt;Long, SubjectDetails&gt;)
     * @return Thymeleaf template {@code hodViewPages/workload}
     */
    @GetMapping("/hod/workload")
    public String showWorkloadPage(Model model) {
        User hod = getLoggedInUser();
        Long deptId = hod.getDeptID();

        List<User> teachers = userService.getMembersByDepartmentAndRole(deptId, "TEACHER");
        List<String> teacherIds = teachers.stream().map(User::getUserId).toList();
        List<TeacherSubject> assignments = userService.getAssignmentsByTeacherIds(teacherIds);

        List<Long> subjectIds = assignments.stream().map(TeacherSubject::getSubjectId).distinct().toList();
        Map<Long, SubjectDetails> subjectIndex = userService.getSubjectName(subjectIds).stream()
                .collect(Collectors.toMap(SubjectDetails::getSubjectId, s -> s));

        Map<String, List<SubjectDetails>> byTeacher = new LinkedHashMap<>();
        for (User t : teachers) {
            byTeacher.put(t.getUserId(), new java.util.ArrayList<>());
        }
        for (TeacherSubject ts : assignments) {
            SubjectDetails sd = subjectIndex.get(ts.getSubjectId());
            if (sd != null) {
                byTeacher.computeIfAbsent(ts.getTeacherId(), k -> new java.util.ArrayList<>()).add(sd);
            }
        }

        model.addAttribute("teachers", teachers);
        model.addAttribute("assignmentsByTeacher", byTeacher);
        return "hodViewPages/workload";
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Returns the {@link User} entity for the currently authenticated principal.
     *
     * @return the logged-in HOD's user record
     */
    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserByUserId(username);
    }

    /**
     * Returns {@code true} if the given course belongs to the supplied department.
     * Callers are expected to resolve the HOD's department once and pass it in,
     * avoiding a redundant {@code getLoggedInUser()} DB call inside this helper.
     *
     * @param courseId  course to check
     * @param hodDeptId the HOD's department ID, obtained from {@link #getLoggedInUser()}
     * @return {@code true} if the course's department matches {@code hodDeptId}
     */
    private boolean ownsCourse(Long courseId, Long hodDeptId) {
        CourseDetails course = userService.getCourseById(courseId);
        return course != null && course.getDepartmentId().equals(hodDeptId);
    }
}
