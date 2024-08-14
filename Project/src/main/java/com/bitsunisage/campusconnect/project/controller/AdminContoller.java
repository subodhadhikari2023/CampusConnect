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
//        Fetching the total users
        List<User> users = userService.findAllUsers();
//        Fetching the total roles irrespective of the category
       List<Roles> roles = userService.findAllRoles();
//       Fetching the total number of users present in the system
       Integer totalUsers =userService.totalUsers();


        Integer totalStudents = userService.totalUsers("ROLE_STUDENT");
        Integer totalTeachers = userService.totalUsers("ROLE_TEACHER");
        Integer totalHods = userService.totalUsers("ROLE_HOD");

        // Add attributes to the model if needed
        model.addAttribute("users",users);
        model.addAttribute("roles",roles);
        model.addAttribute("totalUsers",totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalTeachers", totalTeachers);
        model.addAttribute("totalHods", totalHods);
        return "adminViewPages/admin";
    }
    @GetMapping("admin/students")
    public String getStudentsInfo(Model model){
        List<Roles> students = userService.findByRole("ROLE_STUDENT");
        model.addAttribute("students",students);
      return "adminViewPages/getstudents";
    }


}
