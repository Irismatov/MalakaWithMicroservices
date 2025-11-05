package com.malaka.aat.internal.config;

import com.malaka.aat.internal.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
                return Optional.of("system");
            }

            Object principal = authentication.getPrincipal();

            // Handle UserPrincipal (production code)
            if (principal instanceof UserPrincipal userPrincipal) {
                return Optional.of(userPrincipal.getId());
            }

            // Handle Spring Security User (used in tests with @WithMockUser)
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                return Optional.of("test-user");
            }

            return Optional.of("system");
        };
    }
}