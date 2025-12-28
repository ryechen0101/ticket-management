package com.rye.ticket_management.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeMyPasswordRequest(
        @NotBlank @Size(min = 6, max = 72) String oldPassword,
        @NotBlank @Size(min = 6, max = 72) String newPassword
) {}
