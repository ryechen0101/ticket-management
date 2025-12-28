package com.rye.ticket_management.dto.admin;

import com.rye.ticket_management.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String role,
        boolean enabled,
        LocalDateTime createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getRole().name(),
                Boolean.TRUE.equals(u.getEnabled()),
                u.getCreatedAt()
        );
    }
}
