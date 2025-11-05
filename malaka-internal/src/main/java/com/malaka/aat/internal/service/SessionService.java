package com.malaka.aat.internal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.repository.UserRepository;
import com.malaka.aat.internal.security.UserPrincipal;

@RequiredArgsConstructor
@Component
public class SessionService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if  (context == null) {
            return  null;
        }

        Authentication authentication = context.getAuthentication();

        if (authentication == null) {
            return  null;
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(principal.getId()).orElse(null);
    }

    public String getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        if  (context == null) {
            return  null;
        }

        Authentication authentication = context.getAuthentication();

        if (authentication == null) {
            return  null;
        }

        User user = new User();

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            user = userRepository.findById(userPrincipal.getId()).get();
        }

        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User userPrincipal) {
            user = userRepository.findByUsername(userPrincipal.getUsername()).get();
        }

        return user.getId();
    }

}
