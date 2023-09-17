package com.healthtechbd.backend.config;

import com.healthtechbd.backend.security.JWTAuthenticationFilter;
import com.healthtechbd.backend.security.JWTUnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JWTUnauthorizedEntryPoint jwtUnauthorizedEntryPoint;

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling.authenticationEntryPoint(jwtUnauthorizedEntryPoint))
                .sessionManagement(
                        (sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/signin").permitAll()
                .requestMatchers(HttpMethod.POST, "/signup").permitAll()
                .requestMatchers(HttpMethod.GET, "/profile").permitAll()
                .requestMatchers(HttpMethod.POST, "/register/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/user/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/doctor/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/doctor/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/ambulance/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/ambulance/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/ambulanceProvider/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/pharmacy/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/pharmacy/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/hospital/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/hospital/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/dashboard/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/dashboard/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/medicine/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/medicine/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/diagnosis/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/diagnosis/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/add/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/add/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/review/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/statistics/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/update/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/update/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/bkash/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/bkash/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/delete/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/doctorserial/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/doctorserial/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.cors(Customizer.withDefaults());
        return http.build();
    }

}
