package com.bitsunisage.campusconnect.project.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails subodh = User.builder().username("Subodh").password("{noop}test123").roles("STUDENT").build();

        return new InMemoryUserDetailsManager(subodh);

    }
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(configurer -> configurer.
                        anyRequest()
                        .authenticated()
                )
                .formLogin(form ->
                                form.loginPage("/login")
                                        .loginProcessingUrl("/authenticateTheUser").permitAll()

//                ).logout(logout -> logout.permitAll()
                ).logout(LogoutConfigurer::permitAll
                );

        return httpSecurity.build();
    }
}
