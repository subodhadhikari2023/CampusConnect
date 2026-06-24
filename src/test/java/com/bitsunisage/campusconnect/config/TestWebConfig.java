package com.bitsunisage.campusconnect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Test-only MVC configuration that registers static-resource handlers for the paths
 * referenced by Thymeleaf templates. Without this, Thymeleaf's {@code @{}} link
 * expressions invoke Spring's {@code ResourceUrlProvider}, which throws
 * {@code NoResourceFoundException} in the {@code @WebMvcTest} context when the
 * CSS paths are not mapped to a resource location on the test classpath.
 *
 * <p>Import this class into any {@code @WebMvcTest} that renders a view containing
 * {@code th:href="@{/CSS/...}"} or {@code th:href="@{/vendor/...}"} links.</p>
 */
@Configuration
public class TestWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/CSS/**")
                .addResourceLocations("classpath:/static/CSS/");
        registry.addResourceHandler("/loginResources/**")
                .addResourceLocations("classpath:/static/loginResources/");
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/vendor/");
    }
}
