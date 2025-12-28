package com.rye.ticket_management.controller;

import com.rye.ticket_management.dto.history.ChangeHistoryResponse;
import com.rye.ticket_management.enums.TicketStatus;
import com.rye.ticket_management.service.TicketChangeHistoryService;
import com.rye.ticket_management.service.TicketStatusService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets/{ticketId}")
public class TicketStatusController {

    private final TicketStatusService statusService;
    private final TicketChangeHistoryService changeHistoryService;

    public TicketStatusController(TicketStatusService statusService,
                                  TicketChangeHistoryService changeHistoryService) {
        this.statusService = statusService;
        this.changeHistoryService = changeHistoryService;
    }

    // 改狀態
    @PatchMapping("/status/{toStatus}")
    public String changeStatus(
            @PathVariable Long ticketId,
            @PathVariable TicketStatus toStatus,
            @RequestParam(required = false) String note
    ) {
        statusService.changeStatus(ticketId, toStatus, note);
        return "Status updated";
    }

    // GET /api/tickets/{ticketId}/history?page=0&size=5
    @GetMapping("/history")
    public Page<ChangeHistoryResponse> changeHistory(
            @PathVariable Long ticketId,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return changeHistoryService.getHistoryByTicket(ticketId, pageable);
    }
}
