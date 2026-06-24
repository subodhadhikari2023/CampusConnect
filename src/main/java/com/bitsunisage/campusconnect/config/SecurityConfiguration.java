package com.bitsunisage.campusconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

/**
 * Spring Security configuration for CampusConnect.
 *
 * <p>Configures URL-based role authorization, form login with a custom success handler,
 * and JDBC-backed authentication against the {@code members} and {@code roles} tables.
 * Static assets and public paths are opened via {@code permitAll()} inside the filter chain
 * rather than {@code WebSecurityCustomizer#ignoring}, so security headers still apply.</p>
 */
@Configuration
public class SecurityConfiguration {

    /**
     * Defines the main security filter chain.
     *
     * <ul>
     *   <li>Static assets and public paths — {@code permitAll()}</li>
     *   <li>{@code /student/**} — requires {@code ROLE_STUDENT}</li>
     *   <li>{@code /teacher/**} — requires {@code ROLE_TEACHER}</li>
     *   <li>{@code /admin/**}   — requires {@code ROLE_ADMIN}</li>
     *   <li>{@code /hod/**}     — requires {@code ROLE_HOD}</li>
     *   <li>All other requests  — require any authenticated user</li>
     * </ul>
     *
     * <p>CSRF is disabled because the application does not currently use CSRF tokens
     * in its Thymeleaf forms.</p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the built {@link SecurityFilterChain}
     * @throws Exception if Spring Security configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                        .requestMatchers(
                                "/loginResources/**", "/vendor/**",
                                "/images/**", "/CSS/**", "/JS/**", "/view-data").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hod/**").hasRole("HOD")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticateTheUser")
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout.permitAll()
                        .logoutSuccessUrl("/"))
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/access-denied"))
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login"))
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Provides the custom success handler that redirects users to their role-specific home page.
     *
     * @return a {@link CustomAuthenticationSuccessHandler} instance
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    /**
     * Exposes the application-wide {@link AuthenticationManager} as a bean so it
     * can be injected into {@link JdbcUserDetailsManager} for password re-authentication.
     *
     * @param config the {@link AuthenticationConfiguration} provided by Spring Boot
     * @return the configured {@link AuthenticationManager}
     * @throws Exception if the manager cannot be built
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures JDBC-backed authentication against the application's custom schema.
     * Custom queries map Spring Security's expected column names to the actual
     * {@code members} and {@code roles} table columns.
     *
     * <p>{@code @Lazy} on the {@link AuthenticationManager} parameter breaks the
     * circular dependency: {@link JdbcUserDetailsManager} is a {@code UserDetailsService}
     * used by the {@link AuthenticationManager}, while also needing it for
     * password re-authentication.</p>
     *
     * @param dataSource            the application {@link DataSource} injected by Spring Boot
     * @param authenticationManager the {@link AuthenticationManager} for re-auth on password change
     * @return a configured {@link UserDetailsManager}
     */
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource,
                                                 @Lazy AuthenticationManager authenticationManager) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "SELECT user_id, pw, active FROM members WHERE user_id=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT user_id, role FROM roles WHERE user_id=?");
        jdbcUserDetailsManager.setAuthenticationManager(authenticationManager);
        return jdbcUserDetailsManager;
    }
}
