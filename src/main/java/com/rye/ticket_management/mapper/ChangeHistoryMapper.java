package com.rye.ticket_management.mapper;

import com.rye.ticket_management.dto.history.ChangeHistoryResponse;
import com.rye.ticket_management.entity.TicketChangeHistory;

public class ChangeHistoryMapper {
    private ChangeHistoryMapper() {}

    public static ChangeHistoryResponse toResponse(TicketChangeHistory h) {
        return new ChangeHistoryResponse(
                h.getId(),
                h.getTicket() != null ? h.getTicket().getId() : null,
                h.getFieldName().name(),
                h.getFromValue(),
                h.getToValue(),
                h.getChangedBy() != null ? h.getChangedBy().getUsername() : null,
                h.getNote(),
                h.getCreatedAt()
        );
    }
}
