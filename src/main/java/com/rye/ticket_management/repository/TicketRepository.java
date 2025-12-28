package com.rye.ticket_management.repository;

import com.rye.ticket_management.entity.Ticket;
import com.rye.ticket_management.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select t from Ticket t join fetch t.requester where t.id = :id")
    Optional<Ticket> findByIdWithRequester(@Param("id") Long id);

    @EntityGraph(attributePaths = {"requester"})
    @Query("""
        select t from Ticket t
        where (:status is null or t.status = :status)
          and (
            :q is null or :q = '' or
            lower(t.title) like lower(concat('%', :q, '%')) or
            lower(t.description) like lower(concat('%', :q, '%'))
          )
        """)
    Page<Ticket> search(
            @Param("status") TicketStatus status,
            @Param("q") String q,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"requester"})
    @Query("""
        select t from Ticket t
        where t.requester.id = :requesterId
          and (:status is null or t.status = :status)
          and (
            :q is null or :q = '' or
            lower(t.title) like lower(concat('%', :q, '%')) or
            lower(t.description) like lower(concat('%', :q, '%'))
          )
        """)
    Page<Ticket> searchByRequester(
            @Param("requesterId") Long requesterId,
            @Param("status") TicketStatus status,
            @Param("q") String q,
            Pageable pageable
    );
}
