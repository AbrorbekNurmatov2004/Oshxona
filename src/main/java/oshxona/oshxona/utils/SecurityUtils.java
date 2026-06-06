package oshxona.oshxona.utils;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import oshxona.oshxona.config.CustomUserDetails;

@Component("securityUtils")
public class SecurityUtils {

    public static final String[] WHITE_LIST = {
            "/login",
            "/register",
            "/logout",
            "/static/**",
            "/images/**","/css/**", "/js/**"
    };

    public static CustomUserDetails sessionUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        throw new AuthenticationCredentialsNotFoundException("User not found");
    }

    public boolean superAdmin() {
        try {
            return sessionUser().getSuperAdmin();
        } catch (Exception e) {
            return false;
        }
    }
}
