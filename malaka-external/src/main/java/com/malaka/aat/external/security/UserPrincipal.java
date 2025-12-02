package com.malaka.aat.external.security;

import com.malaka.aat.external.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserPrincipal implements UserDetails {

    private String id;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Additional user information for logging and audit
    private String firstName;
    private String lastName;
    private String middleName;
    private String pinfl;
    private String phone;
    private Long lang;


    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        });

        UserPrincipal userPrincipal = new UserPrincipal();
        userPrincipal.setId(user.getId());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setAuthorities(authorities);
        userPrincipal.setFirstName(user.getFirstName());
        userPrincipal.setLastName(user.getLastName());
        userPrincipal.setMiddleName(user.getMiddleName());
        userPrincipal.setPinfl(user.getPinfl());
        userPrincipal.setPhone(user.getPhone());
        userPrincipal.setLang(user.getLang().getId());
        return userPrincipal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return pinfl;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
