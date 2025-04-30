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
    private UserRepository userRepository;

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @PostMapping("/create-ticket")
    public String createTicket(
            @RequestBody TicketRequest request) throws JsonProcessingException {
        User user = userRepository.findUserByEmail(request.getEmail());
        if(user.getTechnician() == true){
            return "technician cannot create ticket";
        }
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID().toString());
        ticket.setEmail(request.getEmail());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(request.getStatus());
        ticket.setProvider(request.getProvider());
        ticketRepository.save(ticket);
        return ticket.getId();
    }

    @PostMapping("/update-ticket")
    public String updateTicket(
            @RequestBody TicketRequest request) throws JsonProcessingException {
        Ticket ticket = ticketRepository.findTicketById(request.getTicketId());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setStatus(request.getStatus() != null ? request.getStatus() : ticket.getStatus());
        ticket.setCategory(request.getCategory() != null ? request.getCategory() : ticket.getCategory());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : ticket.getPriority());
        ticket.setTechnician(request.getTechnician() != null ? request.getTechnician() : ticket.getTechnician());
        ticketRepository.save(ticket);
        return ticket.getId();
    }

    @GetMapping("/all-tickets")
    public List<Ticket> getAllTickets(){
        return ticketRepository.findAll();
    }

    @GetMapping("/tickets/{id}")
    public Ticket getTicket(@PathVariable String id){
        return ticketRepository.findTicketById(id);
    }

    @GetMapping("/technicians")
    public List<User> getAllTechnicians(@RequestParam Provider source){
        return userRepository.findByTechnicianTrueAndSource(source);
    }
}

