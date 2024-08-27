package com.bitsunisage.campusconnect.project.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration {
    //  Allowing to access the resources without authentication and authorization for login form
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/loginResources/**");
    }


    //    Role based access control of the resources
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hod/**").hasRole("HOD")
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/authenticateTheUser")
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())

                ).logout(LogoutConfigurer::permitAll)

                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Redirect to custom access denied page
                            response.sendRedirect("/access-denied");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Redirect to custom error page for unauthorized access
                            response.sendRedirect("/NoUrlFound");
                        })
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }


    // Add support for JDBC ... no more hard coded users
//    Query to fetch from custom tables
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        // Define query to fetch user from database for custom tables
        jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT user_id, pw, active FROM members WHERE user_id=?");
        //Define query to fetch roles by username
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT user_id,role from roles where USER_id=?");


        return jdbcUserDetailsManager;
    }
}
