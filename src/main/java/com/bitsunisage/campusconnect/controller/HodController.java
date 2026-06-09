package com.bitsunisage.campusconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles HTTP requests for the Head of Department (HOD) role.
 * All routes under {@code /hod/**} require {@code ROLE_HOD} authority,
 * enforced by Spring Security.
 */
@Controller
public class HodController {

    /**
     * Renders the HOD dashboard home page.
     *
     * @return Thymeleaf template name for the HOD home view
     */
    @GetMapping("/hod")
    public String homePage() {
        return "hodViewPages/hod";
    }

    /**
     * Renders the add-course form for the HOD.
     *
     * @return Thymeleaf template name for the add-course view
     */
    @GetMapping("/hodViewPages/add-course")
    public String showAddCoursePage() {
        return "hodViewPages/add-course";
    }

    /**
     * Renders the manage-courses page for the HOD.
     *
     * @return Thymeleaf template name for the manage-course view
     */
    @GetMapping("/hodViewPages/manage-course")
    public String showManageCoursePage() {
        return "hodViewPages/manage-course";
    }
}
