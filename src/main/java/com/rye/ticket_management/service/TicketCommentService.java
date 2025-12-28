package com.rye.ticket_management.service;

import com.rye.ticket_management.dto.comment.AddCommentRequest;
import com.rye.ticket_management.dto.comment.CommentResponse;
import com.rye.ticket_management.entity.Ticket;
import com.rye.ticket_management.entity.TicketComment;
import com.rye.ticket_management.entity.User;
import com.rye.ticket_management.exception.ResourceNotFoundException;
import com.rye.ticket_management.exception.UnauthorizedException;
import com.rye.ticket_management.mapper.CommentMapper;
import com.rye.ticket_management.repository.TicketCommentRepository;
import com.rye.ticket_management.repository.TicketRepository;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketCommentService(
            TicketCommentRepository commentRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
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

    public CommentResponse addComment(Long ticketId, AddCommentRequest req) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到工單: " + ticketId));

        assertCanAccessTicket(ticket);

        TicketComment c = new TicketComment();
        c.setTicket(ticket);
        c.setAuthor(me());
        c.setContent(req.content());

        TicketComment saved = commentRepository.save(c);
        return CommentMapper.toResponse(saved);
    }

    public Page<CommentResponse> getCommentsByTicket(Long ticketId, Pageable pageable) {
        return commentRepository.findByTicketId(ticketId, pageable)
                .map(CommentMapper::toResponse);
    }
}
