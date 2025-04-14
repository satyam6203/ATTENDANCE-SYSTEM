package com.satyam.Attendence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AttendanceConfig {

    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->{
                    auth.requestMatchers("/AllStudents","/students/{id}","")
                            .authenticated();
                    auth.anyRequest().permitAll();
                });
        return httpSecurity.build();
    }
}
