package com.bitsunisage.campusconnect.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.Year;

/**
 * Injects common model attributes into every MVC response so templates
 * do not need to request them individually from each controller.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    /**
     * @return the current calendar year, used in footer copyright notices
     */
    @ModelAttribute("currentYear")
    public int currentYear() {
        return Year.now().getValue();
    }

    /**
     * @param request the current HTTP request
     * @return the URI path of the current request, used for active-link detection in nav fragments
     */
    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
