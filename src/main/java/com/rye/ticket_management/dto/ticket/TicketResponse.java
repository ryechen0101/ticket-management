package com.rye.ticket_management.dto.ticket;

import com.rye.ticket_management.enums.TicketPriority;
import com.rye.ticket_management.enums.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        Long requesterId,
        String requesterUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
