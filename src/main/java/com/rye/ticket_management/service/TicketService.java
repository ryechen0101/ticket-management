package com.rye.ticket_management.service;

import com.rye.ticket_management.dto.ticket.CreateTicketRequest;
import com.rye.ticket_management.dto.ticket.TicketDetailResponse;
import com.rye.ticket_management.dto.ticket.TicketResponse;
import com.rye.ticket_management.dto.ticket.UpdateTicketRequest;
import com.rye.ticket_management.entity.Ticket;
import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.enums.TicketStatus;
import com.rye.ticket_management.exception.ResourceNotFoundException;
import com.rye.ticket_management.exception.UnauthorizedException;
import com.rye.ticket_management.mapper.TicketMapper;
import com.rye.ticket_management.repository.TicketRepository;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketCommentService ticketCommentService;
    private final TicketStatusService ticketStatusService;
    private final TicketChangeHistoryService changeHistoryService;

    public TicketService(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            TicketCommentService ticketCommentService,
            TicketStatusService ticketStatusService,
            TicketChangeHistoryService changeHistoryService
    ) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketCommentService = ticketCommentService;
        this.ticketStatusService = ticketStatusService;
        this.changeHistoryService = changeHistoryService;
    }

    private Ticket getTicketEntityWithRequesterOrThrow(Long id) {
        return ticketRepository.findByIdWithRequester(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到 Ticket: " + id));
    }

    private boolean isAgent() {
        return AuthUtil.isAgent();
    }

    public TicketResponse createTicket(CreateTicketRequest req) {
        User me = AuthUtil.currentUserEntity(userRepository);

        Ticket t = new Ticket();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setStatus(TicketStatus.OPEN);
        t.setPriority(req.priority());
        t.setRequester(me);

        Ticket saved = ticketRepository.save(t);

        changeHistoryService.logChange(saved,
                com.rye.ticket_management.entity.TicketChangeHistory.FieldName.STATUS,
                null, saved.getStatus().name(),
                "建立工單");

        changeHistoryService.logChange(saved,
                com.rye.ticket_management.entity.TicketChangeHistory.FieldName.PRIORITY,
                null, saved.getPriority().name(),
                "建立工單");

        Ticket full = getTicketEntityWithRequesterOrThrow(saved.getId());
        return TicketMapper.toResponse(full);
    }

    public Page<TicketResponse> searchTickets(TicketStatus status, String q, Pageable pageable) {
        if (isAgent()) {
            return ticketRepository.search(status, q, pageable)
                    .map(TicketMapper::toResponse);
        }

        User me = AuthUtil.currentUserEntity(userRepository);
        return ticketRepository.searchByRequester(me.getId(), status, q, pageable)
                .map(TicketMapper::toResponse);
    }

    public TicketResponse getTicketById(Long id) {
        Ticket t = getTicketEntityWithRequesterOrThrow(id);
        assertCanAccess(t);
        return TicketMapper.toResponse(t);
    }

    public TicketResponse updateTicket(Long id, UpdateTicketRequest req) {
        Ticket existing = getTicketEntityWithRequesterOrThrow(id);
        assertCanAccess(existing);

        String oldTitle = existing.getTitle();
        String oldDesc = existing.getDescription();
        String oldPriority = existing.getPriority() != null ? existing.getPriority().name() : null;

        existing.setTitle(req.title());
        existing.setDescription(req.description());
        existing.setPriority(req.priority());

        Ticket saved = ticketRepository.save(existing);

        if (!oldTitle.equals(saved.getTitle())) {
            changeHistoryService.logChange(saved,
                    com.rye.ticket_management.entity.TicketChangeHistory.FieldName.TITLE,
                    oldTitle, saved.getTitle(),
                    null);
        }

        if ((oldDesc == null ? "" : oldDesc).equals(saved.getDescription() == null ? "" : saved.getDescription()) == false) {
            changeHistoryService.logChange(saved,
                    com.rye.ticket_management.entity.TicketChangeHistory.FieldName.DESCRIPTION,
                    oldDesc, saved.getDescription(),
                    null);
        }

        String newPriority = saved.getPriority() != null ? saved.getPriority().name() : null;
        if ((oldPriority == null ? "" : oldPriority).equals(newPriority == null ? "" : newPriority) == false) {
            changeHistoryService.logChange(saved,
                    com.rye.ticket_management.entity.TicketChangeHistory.FieldName.PRIORITY,
                    oldPriority, newPriority,
                    null);
        }

        Ticket full = getTicketEntityWithRequesterOrThrow(id);
        return TicketMapper.toResponse(full);
    }

    public void deleteTicket(Long id) {
        Ticket t = getTicketEntityWithRequesterOrThrow(id);
        assertCanAccess(t);
        ticketRepository.deleteById(id);
    }

    public TicketDetailResponse getTicketDetail(Long ticketId) {
        Ticket t = getTicketEntityWithRequesterOrThrow(ticketId);
        assertCanAccess(t);

        TicketResponse ticket = TicketMapper.toResponse(t);

        var comments = ticketCommentService
                .getCommentsByTicket(ticketId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

        var history = changeHistoryService
                .getHistoryByTicket(ticketId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();

        return new TicketDetailResponse(ticket, comments, history);
    }

    private void assertCanAccess(Ticket ticket) {
        if (isAgent()) return;

        String username = AuthUtil.currentUsername();
        if (ticket.getRequester() == null || ticket.getRequester().getUsername() == null) {
            throw new ResourceNotFoundException("無法判斷工單擁有者");
        }
        if (!ticket.getRequester().getUsername().equals(username)) {
            throw new UnauthorizedException("禁止存取：只能操作自己的工單");
        }
    }
}
