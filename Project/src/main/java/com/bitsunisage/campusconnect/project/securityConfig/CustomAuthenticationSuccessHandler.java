package com.bitsunisage.campusconnect.project.securityConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,   Authentication authentication) throws IOException, ServletException {

        String redirectURL=request.getContextPath();


        if (authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_TEACHER")))
        {
            redirectURL="/teacher";
        } else if (authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_STUDENT"))) {
            redirectURL="/student";
            
        }else if (authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectURL="/admin";

        }else
            redirectURL = "/student";

        request.getSession().setAttribute("url_prior_login", redirectURL);
        response.sendRedirect(redirectURL);
    }
}
