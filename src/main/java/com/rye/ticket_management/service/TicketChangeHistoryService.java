package com.rye.ticket_management.service;

import com.rye.ticket_management.dto.history.ChangeHistoryResponse;
import com.rye.ticket_management.entity.Ticket;
import com.rye.ticket_management.entity.TicketChangeHistory;
import com.rye.ticket_management.mapper.ChangeHistoryMapper;
import com.rye.ticket_management.repository.TicketChangeHistoryRepository;
import com.rye.ticket_management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TicketChangeHistoryService {

    private final TicketChangeHistoryRepository repo;
    private final UserRepository userRepository;

    public TicketChangeHistoryService(TicketChangeHistoryRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public void logChange(Ticket ticket,
                          TicketChangeHistory.FieldName field,
                          String fromValue,
                          String toValue,
                          String note) {

        TicketChangeHistory h = new TicketChangeHistory();
        h.setTicket(ticket);
        h.setFieldName(field);
        h.setFromValue(fromValue);
        h.setToValue(toValue);
        h.setChangedBy(AuthUtil.currentUserEntity(userRepository));
        h.setNote(note);

        repo.save(h);
    }

    public Page<ChangeHistoryResponse> getHistoryByTicket(Long ticketId, Pageable pageable) {
        return repo.findByTicketId(ticketId, pageable)
                .map(ChangeHistoryMapper::toResponse);
    }
}
