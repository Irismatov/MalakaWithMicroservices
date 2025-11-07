package com.malaka.aat.internal.security;

import com.malaka.aat.internal.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final String[] WHITE_LIST = {
            "/api/auth/**",
            "/h2-console/**",
            "/swagger-ui/**",              // Swagger UI resources
            "/swagger-ui.html",            // Swagger UI HTML page
            "/v3/api-docs/**",             // OpenAPI docs
            "/v3/api-docs",                // OpenAPI docs root
            "/swagger-resources/**",       // Swagger resources
            "/webjars/**",                 // WebJars (Swagger UI dependencies)
            "/uploads/images/**",          // Public access to images
            "/uploads/videos/**",          // Public access to videos
            "/actuator/health",            // Health check endpoint
            "/actuator/info"               // Info endpoint
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, RequestLoggingFilter requestLoggingFilter) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers("/api/spr/course/**").hasAnyRole("METHODIST", "ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/course/**").hasAnyRole("METHODIST", "ADMIN", "FACULTY_HEAD", "SUPER_ADMIN", "TEACHER")
                        .requestMatchers("/api/spr/faculty/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/spr/role/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/module/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "METHODIST", "TEACHER")
                        .requestMatchers("/api/topic/**").hasAnyRole("TEACHER", "ADMIN", "SUPER_ADMIN", "METHODIST")
                        .requestMatchers("/api/student-application/**").hasAnyRole("USER", "STUDENT", "ADMIN", "SUPER_ADMIN")
                        .anyRequest().authenticated()
                )
                .headers(headers -> {
                    headers.frameOptions(
                            HeadersConfigurer.FrameOptionsConfig::disable
                    );
                })
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLoggingFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    // CORS configuration removed - handled at API Gateway level
    // Backend services behind a gateway should not add CORS headers

}
