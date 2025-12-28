package com.rye.ticket_management.dto.history;

import java.time.LocalDateTime;

public record ChangeHistoryResponse(
        Long id,
        Long ticketId,
        String fieldName,     // STATUS / PRIORITY / TITLE / DESCRIPTION
        String fromValue,
        String toValue,
        String changedByUsername,
        String note,
        LocalDateTime createdAt
) {}
