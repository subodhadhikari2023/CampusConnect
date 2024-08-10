package com.bitsunisage.campusconnect.project.controller;

import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;
import com.bitsunisage.campusconnect.project.service.UserService;
import com.google.protobuf.RpcCallback;
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
       Integer totalUsers =userService.totalUsers();


        Integer totalStudents = userService.totalStudents("ROLE_STUDENT");
        Integer totalTeachers = userService.totalStudents("ROLE_TEACHER");
        Integer totalHods = userService.totalStudents("ROLE_HOD");

        // Add attributes to the model if needed
        model.addAttribute("users",users);
        model.addAttribute("roles",roles);
        model.addAttribute("totalUsers",totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalHods", totalHods);
        return "admin";
    }


}
