package com.rye.ticket_management.mapper;

import com.rye.ticket_management.dto.ticket.TicketResponse;
import com.rye.ticket_management.entity.Ticket;

public class TicketMapper {
    private TicketMapper() {}

    public static TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getRequester() != null ? t.getRequester().getId() : null,
                t.getRequester() != null ? t.getRequester().getUsername() : null,
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
