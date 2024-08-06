package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;
import com.bitsunisage.campusconnect.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminContoller {
    private UserService userService;

    @Autowired
    public AdminContoller(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/admin")
    public String getAdminPage(Model model) {
        List<User> users = userService.findAllUsers();
       List<Roles> roles = userService.findAllRoles();
        System.out.println(users);
        System.out.println(roles);
        // Add attributes to the model if needed
        model.addAttribute("users",users);
        model.addAttribute("roles",roles);
        return "admin";
    }

  /*  @PostMapping("/manage-users")
    public String manageUsers(Model model) {
        // Implement user management logic
        model.addAttribute("message", "User management action performed");
        return "admin";
    }

    @PostMapping("/settings")
    public String openSettings(Model model) {
        // Implement settings logic
        model.addAttribute("message", "Settings action performed");
        return "admin";
    }

    @PostMapping("/reports")
    public String viewReports(Model model) {
        // Implement reports logic
        model.addAttribute("message", "Reports action performed");
        return "admin";
    }*/
}
