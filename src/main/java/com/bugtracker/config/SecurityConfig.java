package com.bugtracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                    // Public pages
                    .requestMatchers("/login", "/register", "/saveUser").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                    // ADMIN access
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // TESTER access
                    .requestMatchers("/tester/**").hasRole("TESTER")

                    // DEVELOPER access
                    .requestMatchers("/developer/**").hasRole("DEVELOPER")

                    // Common dashboard
                    .requestMatchers("/dashboard").hasAnyRole("ADMIN","TESTER","DEVELOPER")

                    // Other pages require login
                    .anyRequest().authenticated()
            )

            .formLogin(form -> form
            	    .loginPage("/login")
            	    .successHandler((request, response, authentication) -> {

            	        String role = authentication.getAuthorities().iterator().next().getAuthority();

            	        if(role.equals("ROLE_ADMIN")) {
            	            response.sendRedirect("/admin/dashboard");
            	        }
            	        else if(role.equals("ROLE_TESTER")) {
            	            response.sendRedirect("/tester/dashboard");
            	        }
            	        else {
            	            response.sendRedirect("/developer/dashboard");
            	        }

            	    })
            	    .permitAll()
            	)

            .logout(logout -> logout
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            );

        return http.build();
    }
}