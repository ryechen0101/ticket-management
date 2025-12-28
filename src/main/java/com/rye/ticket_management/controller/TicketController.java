package com.rye.ticket_management.controller;

import com.rye.ticket_management.dto.ticket.CreateTicketRequest;
import com.rye.ticket_management.dto.ticket.TicketDetailResponse;
import com.rye.ticket_management.dto.ticket.TicketResponse;
import com.rye.ticket_management.dto.ticket.UpdateTicketRequest;
import com.rye.ticket_management.enums.TicketStatus;
import com.rye.ticket_management.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     單一入口：同時支援
     查全部：GET /api/tickets
     依狀態：GET /api/tickets?status=OPEN
     關鍵字：GET /api/tickets?q=vpn
     分頁排序：GET /api/tickets?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public Page<TicketResponse> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ticketService.searchTickets(status, q, pageable);
    }

    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest req) {
        return ticketService.createTicket(req);
    }

    @PutMapping("/{id}")
    public TicketResponse updateTicket(@PathVariable Long id, @Valid @RequestBody UpdateTicketRequest req) {
        return ticketService.updateTicket(id, req);
    }

    @DeleteMapping("/{id}")
    public String deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return "Ticket 已刪除";
    }

    @GetMapping("/{id}/detail")
    public TicketDetailResponse getTicketDetail(@PathVariable Long id) {
        return ticketService.getTicketDetail(id);
    }
}
