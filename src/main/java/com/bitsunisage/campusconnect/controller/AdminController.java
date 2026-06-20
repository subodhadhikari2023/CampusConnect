package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.DepartmentDetails;
import com.bitsunisage.campusconnect.entities.FileData;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.Semester;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.StorageService;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles HTTP requests for the Admin role.
 * Provides dashboard statistics, full user management (create, update, delete),
 * department and semester management, file moderation, and course/inactive-user views.
 * All routes under {@code /admin/**} require {@code ROLE_ADMIN} authority.
 */
@Controller
public class AdminController {

    private final UserService userService;
    private final StorageService storageService;

    /**
     * Constructs the controller with all required services injected by Spring.
     *
     * @param userService    service for user, department, and academic-catalogue operations
     * @param storageService service for file upload/download/deletion operations
     */
    @Autowired
    public AdminController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }

    /**
     * Renders the admin dashboard with user counts broken down by role.
     *
     * @param model populated with all users, roles, and per-role counts
     * @return Thymeleaf template {@code adminViewPages/admin}
     */
    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("totalUsers", userService.totalUsers());
        model.addAttribute("totalStudents", userService.totalUsers("ROLE_STUDENT"));
        model.addAttribute("totalTeachers", userService.totalUsers("ROLE_TEACHER"));
        model.addAttribute("totalHods", userService.totalUsers("ROLE_HOD"));
        return "adminViewPages/admin";
    }

    /**
     * Lists all registered students.
     * Resolves user records with a single bulk query to avoid N+1 DB calls.
     *
     * @param model populated with {@code userStudents} — list of {@link User} with ROLE_STUDENT
     * @return Thymeleaf template {@code adminViewPages/getstudents}
     */
    @GetMapping("/admin/students")
    public String getStudentsInfo(Model model) {
        Set<String> studentIds = userService.findByRole("ROLE_STUDENT")
                .stream().map(Roles::getUserId).collect(Collectors.toSet());
        List<User> users = userService.findAllUsers().stream()
                .filter(u -> studentIds.contains(u.getUserId()))
                .collect(Collectors.toList());
        model.addAttribute("userStudents", users);
        return "adminViewPages/getstudents";
    }

    /**
     * Lists all registered teachers.
     * Resolves user records with a single bulk query to avoid N+1 DB calls.
     *
     * @param model populated with {@code userTeachers} — list of {@link User} with ROLE_TEACHER
     * @return Thymeleaf template {@code adminViewPages/getteachers}
     */
    @GetMapping("/admin/teachers")
    public String getTeachersInfo(Model model) {
        Set<String> teacherIds = userService.findByRole("ROLE_TEACHER")
                .stream().map(Roles::getUserId).collect(Collectors.toSet());
        List<User> users = userService.findAllUsers().stream()
                .filter(u -> teacherIds.contains(u.getUserId()))
                .collect(Collectors.toList());
        model.addAttribute("userTeachers", users);
        return "adminViewPages/getteachers";
    }

    /**
     * Lists all registered HODs.
     * Resolves user records with a single bulk query to avoid N+1 DB calls.
     *
     * @param model populated with {@code userHODs} — list of {@link User} with ROLE_HOD
     * @return Thymeleaf template {@code adminViewPages/gethod}
     */
    @GetMapping("/admin/hods")
    public String getHodsInfo(Model model) {
        Set<String> hodIds = userService.findByRole("ROLE_HOD")
                .stream().map(Roles::getUserId).collect(Collectors.toSet());
        List<User> users = userService.findAllUsers().stream()
                .filter(u -> hodIds.contains(u.getUserId()))
                .collect(Collectors.toList());
        model.addAttribute("userHODs", users);
        return "adminViewPages/gethod";
    }

    /**
     * Lists all users whose account is inactive (active flag is false).
     *
     * @param model populated with {@code inactiveUsers} — list of inactive {@link User} records
     * @return Thymeleaf template {@code adminViewPages/inactive-users}
     */
    @GetMapping("/admin/inactive-users")
    public String getInactiveUsers(Model model) {
        List<User> inactiveUsers = userService.findAllUsers().stream()
                .filter(u -> !u.isActive())
                .collect(Collectors.toList());
        model.addAttribute("inactiveUsers", inactiveUsers);
        return "adminViewPages/inactive-users";
    }

    /**
     * Renders the add-user form with an empty {@link User} (active defaults to {@code true})
     * and the full department list for the dropdown.
     *
     * @param model populated with {@code user}, {@code roles}, and {@code departments}
     * @return Thymeleaf template {@code adminViewPages/add-user}
     */
    @GetMapping("/admin/add-user")
    public String addUser(Model model) {
        User newUser = new User();
        newUser.setActive(true);
        model.addAttribute("user", newUser);
        model.addAttribute("roles", new Roles());
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/add-user";
    }

    /**
     * Loads an existing user into the add-user form for editing via a GET link.
     * Clears the password field so the form does not expose the stored encoded value.
     * Redirects with an error flash if the user ID does not exist.
     *
     * @param userId             the login username of the user to edit
     * @param model              populated with the user's current data, role, and departments
     * @param redirectAttributes used to pass an error flash if the user is not found
     * @return Thymeleaf template {@code adminViewPages/add-user}, or redirect to {@code /admin}
     */
    @GetMapping("/admin/editUser")
    public String editUser(@RequestParam("userId") String userId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User user = userService.findUserByUserId(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found: " + userId);
            return "redirect:/admin";
        }
        user.setPassword("");
        model.addAttribute("user", user);
        model.addAttribute("roles", userService.findRoleByUserId(userId));
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/add-user";
    }

    /**
     * Creates or updates a user.
     * <p>
     * Password rules: if the submitted password is blank, the existing stored password is
     * preserved (update case). If it is blank and no existing user is found (new user), the
     * request is rejected. A non-blank password is wrapped with {@code {noop}} unless already
     * prefixed.
     * <p>
     * Redirects with an error flash if the department selection or password is missing.
     *
     * @param user               the user bound from the form
     * @param roles              the role assignment bound from the form
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to the admin dashboard
     */
    @PostMapping("/admin/save")
    /**
     * TODO: describe this member.
     *
     * @param ("user" TODO
     * @return TODO
     */
    public String saveUser(@ModelAttribute("user") User user,
                           @ModelAttribute("roles") Roles roles,
                           RedirectAttributes redirectAttributes) {
        if (user.getDeptID() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a department.");
            return "redirect:/admin";
        }

        String submitted = user.getPassword();
        if (submitted == null || submitted.isBlank()) {
            User existing = userService.findUserByUserId(user.getUserId());
            if (existing != null) {
                user.setPassword(existing.getPassword());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Password is required for new users.");
                return "redirect:/admin";
            }
        } else if (!submitted.startsWith("{noop}")) {
            user.setPassword("{noop}" + submitted);
        }

        Department dept = userService.getDepartmentNameByDepartmentId(user.getDeptID().intValue());
        user.setDepartment(dept.getName());

        userService.save(user);

        roles.setUserId(user.getUserId());
        userService.save(roles);

        userService.deleteDepartmentDetailsByUserName(user.getUserId());
        DepartmentDetails details = new DepartmentDetails();
        details.setUserName(user.getUserId());
        details.setDepartmentId(user.getDeptID().intValue());
        details.setRole(roles.getRole().replace("ROLE_", ""));
        userService.saveDepartmentDetails(details);

        redirectAttributes.addFlashAttribute("successMessage", "User saved successfully.");
        return "redirect:/admin";
    }

    /**
     * Loads an existing user into the add-user form for editing via POST.
     * Kept for backward compatibility; prefer the GET {@code /admin/editUser} endpoint.
     *
     * @param userId the login username of the user to edit
     * @param model  populated with the user's current {@link User}, {@link Roles}, and departments
     * @return Thymeleaf template {@code adminViewPages/add-user} pre-filled with existing values
     */
    @PostMapping("/admin/loadUser")
    public String loadUser(@RequestParam("userId") String userId, Model model) {
        model.addAttribute("user", userService.findUserByUserId(userId));
        model.addAttribute("roles", userService.findRoleByUserId(userId));
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/add-user";
    }

    /**
     * Deletes a user, their role record, and their department-details membership.
     * Blocks self-deletion and redirects with an error flash in that case.
     * Deletion order satisfies FK constraints: department_details → roles → members.
     *
     * @param userId             the login username of the user to delete
     * @param authentication     the currently authenticated principal, used to prevent self-delete
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to the admin dashboard
     */
    @PostMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") String userId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication.getName().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account.");
            return "redirect:/admin";
        }

        userService.deleteDepartmentDetailsByUserName(userId);
        Roles roles = userService.findRoleByUserId(userId);
        userService.deleteRole(roles);
        User user = userService.findUserByUserId(userId);
        userService.deleteUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/admin";
    }

    // ── Departments ──────────────────────────────────────────────────────────

    /**
     * Lists all departments.
     *
     * @param model populated with {@code departments} — list of all {@link Department} records
     * @return Thymeleaf template {@code adminViewPages/departments}
     */
    @GetMapping("/admin/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/departments";
    }

    /**
     * Renders the add-department form with an empty {@link Department}.
     *
     * @param model populated with an empty {@code dept} and the full {@code departments} list
     * @return Thymeleaf template {@code adminViewPages/add-department}
     */
    @GetMapping("/admin/add-department")
    public String addDepartmentForm(Model model) {
        model.addAttribute("dept", new Department());
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/add-department";
    }

    /**
     * Loads an existing department into the form for editing.
     * Redirects with an error flash if the ID does not exist.
     *
     * @param id                 the department primary key
     * @param model              populated with the existing {@code dept} and {@code departments} list
     * @param redirectAttributes used to pass an error flash if the department is not found
     * @return Thymeleaf template {@code adminViewPages/add-department}, or redirect to {@code /admin/departments}
     */
    @GetMapping("/admin/editDepartment")
    public String editDepartmentForm(@RequestParam("id") Long id,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        Department dept = userService.getDepartmentNameByDepartmentId(id.intValue());
        if (dept == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department not found.");
            return "redirect:/admin/departments";
        }
        model.addAttribute("dept", dept);
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/add-department";
    }

    /**
     * Creates or updates a department and redirects back to the department list.
     *
     * @param dept               the department bound from the form
     * @param redirectAttributes used to pass a success flash across the redirect
     * @return redirect to {@code /admin/departments}
     */
    @PostMapping("/admin/save-department")
    public String saveDepartment(@ModelAttribute("dept") Department dept,
                                 RedirectAttributes redirectAttributes) {
        userService.saveDepartment(dept);
        redirectAttributes.addFlashAttribute("successMessage", "Department saved successfully.");
        return "redirect:/admin/departments";
    }

    /**
     * Deletes a department after verifying it has no enrolled members.
     * Redirects with an error flash if members exist; otherwise deletes and flashes success.
     *
     * @param deptId             the department primary key
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to {@code /admin/departments}
     */
    @PostMapping("/admin/delete-department")
    public String deleteDepartment(@RequestParam("deptId") Long deptId,
                                   RedirectAttributes redirectAttributes) {
        if (userService.countMembersByDepartment(deptId) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete a department with enrolled members.");
            return "redirect:/admin/departments";
        }
        Department dept = userService.getDepartmentNameByDepartmentId(deptId.intValue());
        userService.deleteDepartment(dept);
        redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully.");
        return "redirect:/admin/departments";
    }

    // ── Semesters ────────────────────────────────────────────────────────────

    /**
     * Lists all semesters.
     *
     * @param model populated with {@code semesters} — list of all {@link Semester} records
     * @return Thymeleaf template {@code adminViewPages/semesters}
     */
    @GetMapping("/admin/semesters")
    public String listSemesters(Model model) {
        model.addAttribute("semesters", userService.getAllSemesters());
        return "adminViewPages/semesters";
    }

    /**
     * Renders the add-semester form with an empty {@link Semester}.
     *
     * @param model populated with an empty {@code semester} and the full {@code semesters} list
     * @return Thymeleaf template {@code adminViewPages/add-semester}
     */
    @GetMapping("/admin/add-semester")
    public String addSemesterForm(Model model) {
        model.addAttribute("semester", new Semester());
        model.addAttribute("semesters", userService.getAllSemesters());
        return "adminViewPages/add-semester";
    }

    /**
     * Creates or updates a semester and redirects back to the semester list.
     *
     * @param semester           the semester bound from the form
     * @param redirectAttributes used to pass a success flash across the redirect
     * @return redirect to {@code /admin/semesters}
     */
    @PostMapping("/admin/save-semester")
    public String saveSemester(@ModelAttribute("semester") Semester semester,
                               RedirectAttributes redirectAttributes) {
        userService.saveSemester(semester);
        redirectAttributes.addFlashAttribute("successMessage", "Semester saved successfully.");
        return "redirect:/admin/semesters";
    }

    /**
     * Deletes a semester after verifying it has no subjects assigned.
     * Redirects with an error flash if subjects exist; otherwise deletes and flashes success.
     *
     * @param semesterId         the semester primary key
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to {@code /admin/semesters}
     */
    @PostMapping("/admin/delete-semester")
    public String deleteSemester(@RequestParam("semesterId") Long semesterId,
                                 RedirectAttributes redirectAttributes) {
        if (userService.countSubjectsBySemester(semesterId) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Cannot delete a semester that has subjects assigned.");
            return "redirect:/admin/semesters";
        }
        Semester semester = userService.getSemesterById(semesterId);
        userService.deleteSemester(semester);
        redirectAttributes.addFlashAttribute("successMessage", "Semester deleted successfully.");
        return "redirect:/admin/semesters";
    }

    // ── Files (moderation) ───────────────────────────────────────────────────

    /**
     * Lists all uploaded files with department name lookup for display.
     *
     * @param model populated with {@code files} (all {@link FileData} records) and
     *              {@code deptNames} (map of department ID to department name)
     * @return Thymeleaf template {@code adminViewPages/files}
     */
    @GetMapping("/admin/files")
    public String listFiles(Model model) {
        List<FileData> files = storageService.findAll();
        Map<Long, String> deptNames = userService.getAllDepartments().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        model.addAttribute("files", files);
        model.addAttribute("deptNames", deptNames);
        return "adminViewPages/files";
    }

    /**
     * Deletes a file record and its physical file on disk, then redirects to the file list.
     *
     * @param fileId             the {@link FileData} primary key to delete
     * @param redirectAttributes used to pass a success flash across the redirect
     * @return redirect to {@code /admin/files}
     */
    @PostMapping("/admin/delete-file")
    public String deleteFile(@RequestParam("fileId") Long fileId,
                             RedirectAttributes redirectAttributes) {
        storageService.deleteResource(fileId);
        redirectAttributes.addFlashAttribute("successMessage", "File deleted successfully.");
        return "redirect:/admin/files";
    }

    // ── Courses (read-only) ───────────────────────────────────────────────────

    /**
     * Lists all courses with department name lookup for display.
     *
     * @param model populated with {@code courses} (all course records) and
     *              {@code deptNames} (map of department ID to department name)
     * @return Thymeleaf template {@code adminViewPages/courses}
     */
    @GetMapping("/admin/courses")
    public String listCourses(Model model) {
        Map<Long, String> deptNames = userService.getAllDepartments().stream()
                .collect(Collectors.toMap(Department::getId, Department::getName));
        model.addAttribute("courses", userService.getAllCourses());
        model.addAttribute("deptNames", deptNames);
        return "adminViewPages/courses";
    }
}
