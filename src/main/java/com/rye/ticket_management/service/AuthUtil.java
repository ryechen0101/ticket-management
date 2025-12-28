package com.rye.ticket_management.service;

import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.exception.UnauthorizedException;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName();
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public static boolean hasAnyRole(String... roles) {
        for (String r : roles) {
            if (hasRole(r)) return true;
        }
        return false;
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isAgent() {
        return hasRole("AGENT");
    }

    public static User currentUserEntity(UserRepository userRepository) {
        String username = currentUsername();
        if (username == null) throw new UnauthorizedException("尚未登入");
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("使用者不存在或已被刪除"));
    }
}
