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

        // Add attributes to the model if needed
        model.addAttribute("users",users);
        model.addAttribute("roles",roles);
        return "admin";
    }


}
