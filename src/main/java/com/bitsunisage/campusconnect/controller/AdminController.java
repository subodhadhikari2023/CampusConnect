package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.entities.Department;
import com.bitsunisage.campusconnect.entities.Roles;
import com.bitsunisage.campusconnect.entities.User;
import com.bitsunisage.campusconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

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
     *
     * @param model populated with {@code userStudents} — list of {@link User} with ROLE_STUDENT
     * @return Thymeleaf template {@code adminViewPages/getstudents}
     */
    @GetMapping("/admin/students")
    public String getStudentsInfo(Model model) {
        List<Roles> students = userService.findByRole("ROLE_STUDENT");
        List<User> users = new ArrayList<>();
        for (Roles stud : students) {
            users.add(userService.findUserByUserId(stud.getUserId()));
        }
        model.addAttribute("userStudents", users);
        return "adminViewPages/getstudents";
    }

    /**
     * Lists all registered teachers.
     *
     * @param model populated with {@code userTeachers} — list of {@link User} with ROLE_TEACHER
     * @return Thymeleaf template {@code adminViewPages/getteachers}
     */
    @GetMapping("/admin/teachers")
    public String getTeachersInfo(Model model) {
        List<Roles> teacherIds = userService.findByRole("ROLE_TEACHER");
        List<User> users = new ArrayList<>();
        for (Roles r : teacherIds) {
            users.add(userService.findUserByUserId(r.getUserId()));
        }
        model.addAttribute("userTeachers", users);
        return "adminViewPages/getteachers";
    }

    /**
     * Lists all registered HODs.
     *
     * @param model populated with {@code userHODs} — list of {@link User} with ROLE_HOD
     * @return Thymeleaf template {@code adminViewPages/gethod}
     */
    @GetMapping("/admin/hods")
    public String getHodsInfo(Model model) {
        List<Roles> hodIds = userService.findByRole("ROLE_HOD");
        List<User> users = new ArrayList<>();
        for (Roles r : hodIds) {
            users.add(userService.findUserByUserId(r.getUserId()));
        }
        model.addAttribute("userHODs", users);
        return "adminViewPages/gethod";
    }

    /**
     * Renders the add-user form with empty {@link User} and {@link Roles} objects
     * and the full department list for the dropdown.
     *
     * @param model populated with {@code user}, {@code roles}, and {@code departments}
     * @return Thymeleaf template {@code adminViewPages/add-user}
     */
    @GetMapping("/admin/add-user")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", new Roles());
        model.addAttribute("departments", userService.getAllDepartments());
        return "adminViewPages/add-user";
    }

    /**
     * Creates a new user. Prepends the {@code {noop}} prefix to the raw password
     * so Spring Security treats it as plain-text during authentication.
     *
     * @param user  the new user bound from the form
     * @param roles the role assignment bound from the form
     * @return redirect to the admin dashboard
     */
    @PostMapping("/admin/save")
    public String saveUser(@ModelAttribute("user") User user,
                           @ModelAttribute("roles") Roles roles) {
        user.setPassword("{noop}" + user.getPassword());
        userService.save(user);
        userService.save(roles);
        return "redirect:/admin";
    }

    /**
     * Renders a form for selecting which user to update or delete.
     *
     * @param action {@code "update"} or {@code "delete"} — controls which form variant is shown
     * @param model  populated with {@code userList} and {@code action}
     * @return Thymeleaf template {@code adminViewPages/showUserFormForUpdate}
     */
    @GetMapping("/admin/showuserUpdateForm")
    public String showUserUpdateForm(@RequestParam(value = "action") String action, Model model) {
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("action", action);
        return "adminViewPages/showUserFormForUpdate";
    }

    /**
     * Loads an existing user into the add-user form for editing.
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
     * Deletes a user and their role record. The role must be deleted first to satisfy
     * the foreign key constraint from {@code roles} to {@code members}.
     *
     * @param userId the login username of the user to delete
     * @return redirect to the admin dashboard
     */
    @PostMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") String userId) {
        User user = userService.findUserByUserId(userId);
        Roles roles = userService.findRoleByUserId(userId);
        userService.deleteRole(roles);
        userService.deleteUser(user);
        return "redirect:/admin";
    }
}
