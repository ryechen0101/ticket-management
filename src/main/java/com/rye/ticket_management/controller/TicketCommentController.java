package com.rye.ticket_management.controller;

import com.rye.ticket_management.dto.comment.AddCommentRequest;
import com.rye.ticket_management.dto.comment.CommentResponse;
import com.rye.ticket_management.service.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class TicketCommentController {

    private final TicketCommentService commentService;

    public TicketCommentController(TicketCommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public Page<CommentResponse> getComments(
            @PathVariable Long ticketId,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return commentService.getCommentsByTicket(ticketId, pageable);
    }

    @PostMapping
    public CommentResponse addComment(@PathVariable Long ticketId, @Valid @RequestBody AddCommentRequest req) {
        return commentService.addComment(ticketId, req);
    }
}
