package com.rye.ticket_management.repository;

import com.rye.ticket_management.entity.TicketChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketChangeHistoryRepository extends JpaRepository<TicketChangeHistory, Long> {

    @EntityGraph(attributePaths = {"changedBy"})
    @Query("""
      select h from TicketChangeHistory h
      where h.ticket.id = :ticketId
      """)
    Page<TicketChangeHistory> findByTicketId(
            @Param("ticketId") Long ticketId,
            Pageable pageable
    );
}
