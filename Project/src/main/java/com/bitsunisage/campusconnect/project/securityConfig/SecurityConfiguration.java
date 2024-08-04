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

    @Bean
    WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/images/**");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(configurer -> configurer.anyRequest().authenticated()).formLogin(form -> form.loginPage("/").loginProcessingUrl("/authenticateTheUser").permitAll().successHandler(authenticationSuccessHandler())

//                ).logout(logout -> logout.permitAll()
        ).logout(LogoutConfigurer::permitAll);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer.requestMatchers("/student/**").hasRole("STUDENT").requestMatchers("/teacher/**").hasRole("TEACHER").requestMatchers("/admin/**").hasRole("ADMIN")


        );
//        use HTTP Basic Authentication
        http.httpBasic(Customizer.withDefaults());
//        Disable CSRF
//        In general not required for stateless Rest apis that use POST,GET,PUT AND DELETE ...
//        http.csrf(csrf -> csrf.disable()); old way to do the same thing
        http.csrf(AbstractHttpConfigurer::disable);


        return http.build();

    }


    // Add support for JDBC ... no more hard coded users
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        // Define query to fetch user from database for custom tables
        jdbcUserDetailsManager.setUsersByUsernameQuery(" select user_id, pw, active from members where user_id=?");
        //Define query to fetch roles by username
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select user_id, role from roles where user_id=?");


        return jdbcUserDetailsManager;
    }
}
