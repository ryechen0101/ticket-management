package com.rye.ticket_management.dto.ticket;

import com.rye.ticket_management.dto.comment.CommentResponse;
import com.rye.ticket_management.dto.history.ChangeHistoryResponse;

import java.util.List;

public record TicketDetailResponse(
        TicketResponse ticket,
        List<CommentResponse> comments,
        List<ChangeHistoryResponse> history
) {}
