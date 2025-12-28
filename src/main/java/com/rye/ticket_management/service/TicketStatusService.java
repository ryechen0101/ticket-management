package com.rye.ticket_management.service;

import com.rye.ticket_management.entity.Ticket;
import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.enums.TicketStatus;
import com.rye.ticket_management.exception.BadRequestException;
import com.rye.ticket_management.exception.ResourceNotFoundException;
import com.rye.ticket_management.exception.UnauthorizedException;
import com.rye.ticket_management.repository.TicketRepository;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketStatusService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketChangeHistoryService changeHistoryService;

    public TicketStatusService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketChangeHistoryService changeHistoryService
    ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.changeHistoryService = changeHistoryService;
    }

    private User me() {
        return AuthUtil.currentUserEntity(userRepository);
    }

    private boolean isAgent() {
        return AuthUtil.hasRole("AGENT");
    }

    private void assertCanAccessTicket(Ticket ticket) {
        if (AuthUtil.isAgent()) return;

        User me = AuthUtil.currentUserEntity(userRepository);
        if (ticket.getRequester() == null || ticket.getRequester().getId() == null) {
            throw new ResourceNotFoundException("工單資料異常：找不到建立者");
        }
        if (!ticket.getRequester().getId().equals(me.getId())) {
            throw new UnauthorizedException("禁止存取：只能操作自己的工單");
        }
    }

    private boolean isValidTransition(TicketStatus from, TicketStatus to) {
        if (from == null) return to == TicketStatus.OPEN;
        return switch (from) {
            case OPEN -> to == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == TicketStatus.RESOLVED;
            case RESOLVED -> to == TicketStatus.CLOSED || to == TicketStatus.IN_PROGRESS;
            case CLOSED -> to == TicketStatus.IN_PROGRESS;
        };
    }

    public void changeStatus(Long ticketId, TicketStatus toStatus, String note) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到工單: " + ticketId));

        assertCanAccessTicket(ticket);

        TicketStatus fromStatus = ticket.getStatus();
        if (!isValidTransition(fromStatus, toStatus)) {
            throw new BadRequestException("不合法的狀態轉移: " + fromStatus + " -> " + toStatus);
        }

        ticket.setStatus(toStatus);
        ticketRepository.save(ticket);

        changeHistoryService.logChange(ticket,
                com.rye.ticket_management.entity.TicketChangeHistory.FieldName.STATUS,
                fromStatus != null ? fromStatus.name() : null,
                toStatus.name(),
                note);
    }
}
