package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.DepartmentDetails;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.User;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles HTTP requests for the Admin role.
 * Provides dashboard statistics and full user management (create, update, delete).
 * All routes under {@code /admin/**} require {@code ROLE_ADMIN} authority.
 */
@Controller
public class AdminController {

    private UserService userService;

    /**
     * @param userService service for user and role operations
     */
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
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
}
