package com.superops.courier.slack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.superops.courier.slack.entity.*;
import com.superops.courier.slack.model.ReplyRequest;
import com.superops.courier.slack.model.TicketRequest;
import com.superops.courier.slack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        ticket.setSubCategory(request.getSubCategory() != null ? request.getSubCategory(): ticket.getSubCategory());
        ticket.setPriority(request.getPriority() != null ? request.getPriority() : ticket.getPriority());
        ticket.setTechnician(request.getTechnician() != null ? request.getTechnician() : ticket.getTechnician());
        ticketRepository.save(ticket);
        return ticket.getId();
    }

    @GetMapping("/all-tickets")
    public List<Ticket> getAllTickets(){
        return ticketRepository.findAll();
    }

    @GetMapping("/tickets")
    public List<Ticket> getTicketsByEmail(@RequestParam String email) {
        return ticketRepository.findByEmail(email);
    }

    @GetMapping("/tickets/{id}")
    public Ticket getTicket(@PathVariable String id){
        return ticketRepository.findTicketById(id);
    }

    @GetMapping("/technicians")
    public List<User> getAllTechnicians(@RequestParam Provider source){
        return userRepository.findByTechnicianTrueAndSource(source);
    }

    @GetMapping("/ticket/{id}/fields")
    public ResponseEntity<List<Map<String, Object>>> getEditableFields(@PathVariable String id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if (!optionalTicket.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Ticket ticket = optionalTicket.get();
        List<Map<String, Object>> fields = new ArrayList<>();

        fields.add(Map.of(
                "name", "priority",
                "type", "dropdown",
                "options", List.of("LOW", "MEDIUM", "HIGH"),
                "value", ticket.getPriority() != null ? ticket.getPriority().toString().toLowerCase() : "low"
        ));

        fields.add(Map.of(
                "name", "status",
                "type", "dropdown",
                "options", List.of("TODO", "IN_PROGRESS", "DONE", "CANCELED"),
                "value", ticket.getStatus() != null ? ticket.getStatus().toString().toLowerCase() : "todo"
        ));

        fields.add(Map.of(
                "name", "category",
                "type", "category-dropdown",
                "options", List.of("engineering" , "sales" , "technician" , "hr"),
                "value", ticket.getCategory() != null ? ticket.getCategory() : "hardware"
        ));

        fields.add(Map.of(
                "name", "subcategory",
                "type", "subcategory-dropdown",
                "value", ticket.getSubCategory() != null ? ticket.getSubCategory() : ""
        ));

        return ResponseEntity.ok(fields);
    }

    @GetMapping("/ticket/subcategories")
    public ResponseEntity<List<Map<String, String>>> getSubcategories(@RequestParam String category) {
        Map<String, List<String>> categoryMap = Map.of(
                "engineering", List.of("frontend", "backend", "devops", "qa"),
                "sales", List.of("domestic", "international", "enterprise", "smb"),
                "technician", List.of("electrical", "mechanical", "field service", "maintenance"),
                "hr", List.of("recruitment", "payroll", "employee relations", "training")
        );

        List<String> subcategories = categoryMap.getOrDefault(category.toLowerCase(), List.of());

        List<Map<String, String>> response = subcategories.stream()
                .map(sub -> Map.of("title", sub, "value", sub.toLowerCase()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

}

