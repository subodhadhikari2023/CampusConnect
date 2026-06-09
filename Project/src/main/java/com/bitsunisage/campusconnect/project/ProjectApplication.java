package com.bitsunisage.campusconnect.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the CampusConnect Spring Boot application.
 */
@SpringBootApplication
public class ProjectApplication {

    /**
     * Bootstraps and launches the Spring application context.
     *
     * @param args command-line arguments forwarded to Spring
     */
    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }
}
