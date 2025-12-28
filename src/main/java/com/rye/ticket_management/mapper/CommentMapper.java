package com.rye.ticket_management.mapper;

import com.rye.ticket_management.dto.comment.CommentResponse;
import com.rye.ticket_management.entity.TicketComment;

public class CommentMapper {
    private CommentMapper() {}

    public static CommentResponse toResponse(TicketComment c) {
        return new CommentResponse(
                c.getId(),
                c.getTicket() != null ? c.getTicket().getId() : null,
                c.getAuthor() != null ? c.getAuthor().getUsername() : null,
                c.getContent(),
                c.getCreatedAt()
        );
    }
}
