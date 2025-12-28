package com.rye.ticket_management.repository;

import com.rye.ticket_management.entity.TicketComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    // 分頁 + 帶 author（避免 lazy 問題）
    @EntityGraph(attributePaths = {"author"})
    @Query("""
      select c from TicketComment c
      where c.ticket.id = :ticketId
      """)
    Page<TicketComment> findByTicketId(
            @Param("ticketId") Long ticketId,
            Pageable pageable
    );
}
