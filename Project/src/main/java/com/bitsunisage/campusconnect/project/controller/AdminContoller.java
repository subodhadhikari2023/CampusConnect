package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.management.relation.Role;
import java.util.List;

@Controller
public class AdminContoller {
    private UserService userService;

    // Dependency Injection for the user service object
    @Autowired
    public AdminContoller(UserService userService) {
        this.userService = userService;

    }

    // Mapping for the home page of admin role
    @GetMapping("/admin")
    public String getAdminPage(Model model) {
//        Fetching the total users
        List<User> users = userService.findAllUsers();
//        Fetching the total roles irrespective of the category
        List<Roles> roles = userService.findAllRoles();
//       Fetching the total number of users present in the system
        Integer totalUsers = userService.totalUsers();


        Integer totalStudents = userService.totalUsers("ROLE_STUDENT");
        Integer totalTeachers = userService.totalUsers("ROLE_TEACHER");
        Integer totalHods = userService.totalUsers("ROLE_HOD");

        // Add attributes to the model if needed
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalHods", totalHods);
        return "adminViewPages/admin";
    }

    //    Mapping for the page to view the total lists of students
    @GetMapping("/admin/students")
    public String getStudentsInfo(Model model) {
        List<Roles> students = userService.findByRole("ROLE_STUDENT");
        model.addAttribute("students", students);
        return "adminViewPages/getstudents";
    }

    // Mapping for the add user form
    @GetMapping("/admin/add-user")
    public String addUser(Model model) {
        User user = new User();
        Roles roles = new Roles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "adminViewPages/add-user";
    }

    // Mapping to save the user
    @PostMapping("/admin/save")
    public String saveUser(@ModelAttribute("user") User user, @ModelAttribute("roles") Roles roles) {
        user.setPassword("{noop}" + user.getPassword());

        userService.save(user);
        userService.save(roles);
        return "redirect:/admin";
    }

    // Mapping to show user update form
    @GetMapping("/admin/showuserUpdateForm")
    public String showUserUpdateForm(@RequestParam(value = "action") String action, Model model) {
        // To populate the dropdown
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("action", action);
        // Return the view name
        return "adminViewPages/showUserFormForUpdate";
    }

    // Mapping for loading the user
    @PostMapping("/admin/loadUser")
    public String loadUser(@RequestParam("userId") String userId, Model model) {
        User user = userService.findUserByUserId(userId);
        Roles roles = userService.findRoleByUserId(userId);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
//        For debugging purpose
//        System.out.println(user.getPassword());

        return "adminViewPages/add-user";

    }

    @PostMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") String userId) {
        User user = userService.findUserByUserId(userId);
        Roles roles = userService.findRoleByUserId(userId);
        userService.deleteRole(roles);
        userService.deleteUser(user);

        return "redirect:/admin";

    }
}



