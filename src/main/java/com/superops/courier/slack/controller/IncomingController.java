package com.superops.courier.slack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.superops.courier.slack.entity.*;
import com.superops.courier.slack.model.ReplyRequest;
import com.superops.courier.slack.model.TicketRequest;
import com.superops.courier.slack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class IncomingController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @PostMapping("/create-ticket")
    public String createTicket(
            @RequestBody TicketRequest request) throws JsonProcessingException {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setUserId(request.getUserId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(request.getStatus());
        ticket.setProvider(request.getProvider());
        ticketRepository.save(ticket);
        return ticket.getId();
    }
}

