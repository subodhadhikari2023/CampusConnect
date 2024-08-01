package com.bitsunisage.campusconnect.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/admin")
public class AdminContoller {

    @GetMapping
    public String getAdminPage(Model model) {
        // Add attributes to the model if needed
        return "admin";
    }

    @PostMapping("/manage-users")
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
    }
}
