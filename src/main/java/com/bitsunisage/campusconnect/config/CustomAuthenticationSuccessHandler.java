package com.bitsunisage.campusconnect.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * Redirects an authenticated user to their role-specific home page after a successful login.
 *
 * <p>The chosen URL is stored in the HTTP session under {@code "url_prior_login"} so that
 * {@link com.bitsunisage.campusconnect.controller.MasterController#showLoginPage}
 * can redirect already-authenticated users away from the login page without a second round-trip.</p>
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Determines the user's role and redirects to the corresponding home URL.
     * Falls back to {@code /student} if no recognised role is found.
     *
     * @param request        the current HTTP request
     * @param response       the current HTTP response
     * @param authentication the authenticated principal with its granted authorities
     * @throws IOException      if the redirect fails
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String redirectURL = request.getContextPath();

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            redirectURL = "/teacher";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            redirectURL = "/student";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectURL = "/admin";
        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_HOD"))) {
            redirectURL = "/hod";
        } else {
            redirectURL = "/student";
        }

        request.getSession().setAttribute("url_prior_login", redirectURL);
        response.sendRedirect(redirectURL);
    }
}
