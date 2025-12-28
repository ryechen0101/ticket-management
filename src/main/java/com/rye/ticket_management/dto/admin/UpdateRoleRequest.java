package com.rye.ticket_management.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateRoleRequest(
        @NotBlank
        @Pattern(regexp = "ADMIN|AGENT|USER", message = "role 只能是 ADMIN / AGENT / USER")
        String role
) {}
