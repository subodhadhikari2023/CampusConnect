package com.bitsunisage.campusconnect.config;

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
}
