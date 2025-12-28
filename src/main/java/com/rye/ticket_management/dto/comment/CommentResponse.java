package com.rye.ticket_management.dto.comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long ticketId,
        String authorUsername,
        String content,
        LocalDateTime createdAt
) {}
