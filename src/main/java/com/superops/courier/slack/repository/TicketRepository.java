package com.superops.courier.slack.repository;

import com.superops.courier.slack.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    Ticket findTicketById(String id);
    List<Ticket> findByEmail(String email);
}
