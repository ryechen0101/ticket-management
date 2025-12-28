package com.rye.ticket_management.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 50)
        String username,

        @NotBlank @Size(min = 6, max = 72)
        String password,

        @NotBlank
        @Pattern(regexp = "ADMIN|AGENT|USER", message = "role 只能是 ADMIN / AGENT / USER")
        String role
) {}
