package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.DepartmentDetails;
import com.bitsunisage.campusconnect.entities.FileData;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.Semester;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.exceptions.DepartmentNotFoundException;
import com.bitsunisage.campusconnect.exceptions.UserNotFoundException;
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

import java.util.Comparator;
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
     * Renders the admin dashboard with user, department, and semester counts.
     *
     * @param model populated with per-role user counts, department count, and semester count
     * @return Thymeleaf template {@code adminViewPages/admin}
     */
    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        List<User> allUsers = userService.findAllUsers();
        List<FileData> allFiles = storageService.findAll();

        model.addAttribute("totalUsers", userService.totalUsers());
        model.addAttribute("totalStudents", userService.totalUsers("ROLE_STUDENT"));
        model.addAttribute("totalTeachers", userService.totalUsers("ROLE_TEACHER"));
        model.addAttribute("totalHods", userService.totalUsers("ROLE_HOD"));
        model.addAttribute("totalDepartments", userService.getAllDepartments().size());
        model.addAttribute("totalSemesters", userService.getAllSemesters().size());
        model.addAttribute("totalCourses", userService.getAllCourses().size());

        long inactiveCount = allUsers.stream().filter(u -> !u.isActive()).count();
        model.addAttribute("totalInactive", inactiveCount);

        model.addAttribute("totalFiles", allFiles.size());
        long totalBytes = allFiles.stream().mapToLong(FileData::getFileSize).sum();
        model.addAttribute("storageUsed", formatBytes(totalBytes));

        List<FileData> recentFiles = allFiles.stream()
                .filter(f -> f.getUploadDate() != null)
                .sorted(Comparator.comparing(FileData::getUploadDate).reversed())
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentFiles", recentFiles);

        return "adminViewPages/admin";
    }

    /**
     * Formats a byte count into a human-readable string (B, KB, MB, GB).
     *
     * @param bytes raw byte count
     * @return formatted string such as {@code "4.2 MB"}
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
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
     * Password rules: if the submitted password is blank, the existing stored password is
     * preserved (update case). If it is blank and no existing user is found (new user), the
     * request is rejected. A non-blank password is wrapped with {@code {noop}} unless already
     * prefixed. Redirects with an error flash if the department selection or password is missing.
     *
     * @param user               the user bound from the form
     * @param roles              the role assignment bound from the form
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to the admin dashboard
     */
    @PostMapping("/admin/save")
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
     * Lists all departments with the currently assigned HOD for each.
     *
     * @param model populated with {@code departments} and {@code hodByDept}
     *              (map of department ID to HOD username, may be absent for unassigned departments)
     * @return Thymeleaf template {@code adminViewPages/departments}
     */
    @GetMapping("/admin/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", userService.getAllDepartments());
        Set<String> hodIds = userService.findByRole("ROLE_HOD")
                .stream().map(Roles::getUserId).collect(Collectors.toSet());
        Map<Long, String> hodByDept = userService.findAllUsers().stream()
                .filter(u -> hodIds.contains(u.getUserId()) && u.getDeptID() != null)
                .collect(Collectors.toMap(User::getDeptID, User::getUserId, (a, b) -> a));
        model.addAttribute("hodByDept", hodByDept);
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
     * Loads an existing semester into the add-semester form for editing.
     * Redirects with an error flash if the semester ID does not exist.
     *
     * @param semesterId         the semester primary key
     * @param model              populated with the existing {@code semester}
     * @param redirectAttributes used to pass an error flash if the semester is not found
     * @return Thymeleaf template {@code adminViewPages/add-semester}, or redirect to {@code /admin/semesters}
     */
    @GetMapping("/admin/editSemester")
    public String editSemesterForm(@RequestParam("semesterId") Long semesterId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Semester semester = userService.getSemesterById(semesterId);
        if (semester == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Semester not found.");
            return "redirect:/admin/semesters";
        }
        model.addAttribute("semester", semester);
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

    // ── HOD assignment ───────────────────────────────────────────────────────

    /**
     * Renders the HOD-assignment form for a given department.
     * Populates the form with all current HOD users and marks whichever HOD
     * is currently assigned to this department as pre-selected.
     *
     * @param deptId             the department to assign an HOD to
     * @param model              populated with {@code dept}, {@code hods}, and {@code currentHodId}
     * @param redirectAttributes used to pass an error flash if the department is not found
     * @return Thymeleaf template {@code adminViewPages/assign-hod}
     */
    @GetMapping("/admin/assign-hod")
    public String assignHodForm(@RequestParam("deptId") Long deptId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Department dept = userService.getDepartmentNameByDepartmentId(deptId.intValue());
        if (dept == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department not found.");
            return "redirect:/admin/departments";
        }
        Set<String> hodIds = userService.findByRole("ROLE_HOD")
                .stream().map(Roles::getUserId).collect(Collectors.toSet());
        List<User> hods = userService.findAllUsers().stream()
                .filter(u -> hodIds.contains(u.getUserId()))
                .collect(Collectors.toList());
        String currentHodId = hods.stream()
                .filter(u -> deptId.equals(u.getDeptID()))
                .map(User::getUserId)
                .findFirst().orElse(null);
        model.addAttribute("dept", dept);
        model.addAttribute("hods", hods);
        model.addAttribute("currentHodId", currentHodId);
        return "adminViewPages/assign-hod";
    }

    /**
     * Saves the HOD-to-department assignment by updating the chosen HOD's {@code deptID}
     * and denormalised {@code department} name, then redirecting to the departments list.
     * Redirects with an error flash if the department or HOD user cannot be found.
     *
     * @param deptId             the department to assign the HOD to
     * @param hodUserId          the login username of the HOD to assign
     * @param redirectAttributes used to pass a success/error flash across the redirect
     * @return redirect to {@code /admin/departments}, or back to the assign form on failure
     */
    @PostMapping("/admin/save-hod-assignment")
    public String saveHodAssignment(@RequestParam("deptId") Long deptId,
                                    @RequestParam("hodUserId") String hodUserId,
                                    RedirectAttributes redirectAttributes) {
        Department dept = userService.getDepartmentNameByDepartmentId(deptId.intValue());
        if (dept == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Department not found.");
            return "redirect:/admin/departments";
        }
        User hod = userService.findUserByUserId(hodUserId);
        if (hod == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected HOD user not found: " + hodUserId);
            return "redirect:/admin/assign-hod?deptId=" + deptId;
        }
        hod.setDeptID(deptId);
        hod.setDepartment(dept.getName());
        userService.save(hod);
        redirectAttributes.addFlashAttribute("successMessage",
                hodUserId + " assigned as HOD of " + dept.getName() + ".");
        return "redirect:/admin/departments";
    }

    // ── Toggle active status ─────────────────────────────────────────────────

    /**
     * Flips the active flag of a user account and redirects back to the calling list page.
     * Blocks an admin from changing their own status.
     * The {@code redirectTo} parameter must start with {@code /} to prevent open-redirect attacks.
     *
     * @param userId             the login username of the user to toggle
     * @param redirectTo         the relative URL to redirect back to after the action
     * @param authentication     the currently authenticated principal, used to block self-toggle
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to {@code redirectTo}
     */
    @PostMapping("/admin/toggleActive")
    public String toggleActive(@RequestParam("userId") String userId,
                               @RequestParam(value = "redirectTo", defaultValue = "/admin") String redirectTo,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (!redirectTo.startsWith("/")) {
            redirectTo = "/admin";
        }
        if (authentication.getName().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot change the status of your own account.");
            return "redirect:" + redirectTo;
        }
        User user = userService.findUserByUserId(userId);
        user.setActive(!user.isActive());
        userService.save(user);
        String status = user.isActive() ? "activated" : "deactivated";
        redirectAttributes.addFlashAttribute("successMessage", userId + " has been " + status + ".");
        return "redirect:" + redirectTo;
    }

    // ── Password reset ───────────────────────────────────────────────────────────

    /**
     * Renders the password-reset form for the given user.
     * Redirects to the profile page if the admin tries to reset their own password.
     *
     * @param userId             login username of the user whose password will be reset
     * @param model              populated with {@code targetUser}
     * @param authentication     the currently authenticated admin, used to block self-reset
     * @param redirectAttributes used to pass error/info flash messages across the redirect
     * @return Thymeleaf template {@code adminViewPages/reset-password}, or a redirect
     */
    @GetMapping("/admin/reset-password")
    public String resetPasswordForm(@RequestParam("userId") String userId,
                                    Model model,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        if (authentication.getName().equals(userId)) {
            redirectAttributes.addFlashAttribute("infoMessage", "Use the Profile page to change your own password.");
            return "redirect:/admin/profile";
        }
        User user = userService.findUserByUserId(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found: " + userId);
            return "redirect:/admin";
        }
        model.addAttribute("targetUser", user);
        return "adminViewPages/reset-password";
    }

    /**
     * Applies a new password for the target user after the two entries are confirmed to match.
     * Rejects blank passwords and self-reset attempts.
     *
     * @param userId             login username of the user to update
     * @param newPassword        the desired new password
     * @param confirmPassword    must equal {@code newPassword}
     * @param authentication     the currently authenticated admin
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to the admin dashboard, or back to the form on validation failure
     */
    @PostMapping("/admin/reset-password")
    public String resetPassword(@RequestParam("userId") String userId,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication.getName().equals(userId)) {
            redirectAttributes.addFlashAttribute("infoMessage", "Use the Profile page to change your own password.");
            return "redirect:/admin/profile";
        }
        if (newPassword.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password cannot be blank.");
            return "redirect:/admin/reset-password?userId=" + userId;
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/admin/reset-password?userId=" + userId;
        }
        User user = userService.findUserByUserId(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found: " + userId);
            return "redirect:/admin";
        }
        user.setPassword("{noop}" + newPassword);
        userService.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Password for " + userId + " has been reset.");
        return "redirect:/admin";
    }

    // ── Admin profile ─────────────────────────────────────────────────────────

    /**
     * Renders the admin's own profile page for editing their email and password.
     * Clears the password field so the stored encoded value is never exposed to the browser.
     *
     * @param authentication the currently authenticated admin
     * @param model          populated with {@code admin} — the current admin's {@link User} record
     * @return Thymeleaf template {@code adminViewPages/profile}
     */
    @GetMapping("/admin/profile")
    public String profilePage(Authentication authentication, Model model) {
        User admin = userService.findUserByUserId(authentication.getName());
        if (admin == null) {
            throw new UserNotFoundException(authentication.getName());
        }
        admin.setPassword("");
        model.addAttribute("admin", admin);
        return "adminViewPages/profile";
    }

    /**
     * Saves changes to the admin's own email and, optionally, their password.
     * A blank new-password field leaves the existing password unchanged.
     * Redirects back to the profile page with a validation error if the two password entries differ.
     *
     * @param email              the updated email address
     * @param newPassword        optional new password; blank means no change
     * @param confirmPassword    must equal {@code newPassword} when non-blank
     * @param authentication     the currently authenticated admin
     * @param redirectAttributes used to pass success/error flash messages across the redirect
     * @return redirect to {@code /admin/profile}
     */
    @PostMapping("/admin/save-profile")
    public String saveProfile(@RequestParam("email") String email,
                              @RequestParam("newPassword") String newPassword,
                              @RequestParam("confirmPassword") String confirmPassword,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (!newPassword.isBlank() && !newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/admin/profile";
        }
        User admin = userService.findUserByUserId(authentication.getName());
        if (admin == null) {
            throw new UserNotFoundException(authentication.getName());
        }
        admin.setEmail(email);
        if (!newPassword.isBlank()) {
            admin.setPassword("{noop}" + newPassword);
        }
        userService.save(admin);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/admin/profile";
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
