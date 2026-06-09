package com.bitsunisage.campusconnect.controller;

import com.bitsunisage.campusconnect.exceptions.AccessDeniedCustomException;
import com.bitsunisage.campusconnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles public and cross-cutting HTTP routes: the landing page, login, error pages,
 * and a debug data view. Implements {@link ErrorController} so Spring Boot delegates
 * {@code /error} to this controller rather than its default white-label page.
 */
@Controller
public class MasterController implements ErrorController {

    private final UserService userService;

    /**
     * @param userService service used to populate the debug data view
     */
    @Autowired
    public MasterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Renders the public landing page.
     *
     * @return Thymeleaf template {@code index}
     */
    @GetMapping("/")
    public String showPublicLandingPage() {
        return "index";
    }

    /**
     * Shows the login form, or redirects an already-authenticated user back to their
     * role home page if the session contains a {@code url_prior_login} attribute.
     *
     * @param request the current HTTP request; checked for the session redirect attribute
     * @return a redirect string if authenticated, or the {@code loginFile} template name
     */
    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request) {
        String redirectURL = (String) request.getSession().getAttribute("url_prior_login");
        if (redirectURL != null) {
            return "redirect:" + redirectURL;
        }
        return "loginFile";
    }

    /**
     * Triggers a 403 error view by throwing {@link AccessDeniedCustomException},
     * which is handled by {@link com.bitsunisage.campusconnect.exceptions.CustomExceptionHandler}.
     */
    @GetMapping("/access-denied")
    public void accessDeniedErrorMapping() {
        throw new AccessDeniedCustomException();
    }

    /**
     * Triggers a 500 error view by throwing a {@link RuntimeException},
     * which is handled by {@link com.bitsunisage.campusconnect.exceptions.CustomExceptionHandler}.
     */
    @GetMapping("/error")
    public void error() {
        throw new RuntimeException();
    }

    /**
     * Debug view that exposes all users, roles, and departments as model attributes.
     * Accessible without authentication (bypassed in {@link com.bitsunisage.campusconnect.config.SecurityConfiguration}).
     *
     * @param model the Spring MVC model to populate
     * @return Thymeleaf template {@code ReferralData}
     */
    @GetMapping("/view-data")
    public String viewData(Model model) {
        model.addAttribute("members", userService.findAllUsers());
        model.addAttribute("roles", userService.findAllRoles());
        model.addAttribute("departments", userService.getAllDepartments());
        return "ReferralData";
    }
}
