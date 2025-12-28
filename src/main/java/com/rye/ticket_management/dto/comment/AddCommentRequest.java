package com.rye.ticket_management.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(
        @NotBlank(message = "留言不能是空白")
        @Size(max = 500, message = "留言長度不能超過 500 個字元")
        String content
) {}
