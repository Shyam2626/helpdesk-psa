package com.superops.courier.slack.controller;

import com.superops.courier.slack.entity.Ticket;
import com.superops.courier.slack.entity.TicketMessage;
import com.superops.courier.slack.model.ReplyRequest;
import com.superops.courier.slack.repository.TicketMessageRepository;
import com.superops.courier.slack.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OutgoingController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @PostMapping("/ticket/{id}/replyToUser")
    public String replyToTicket(
            @PathVariable String id,
            @RequestParam("message") String message,
            @RequestParam("userId") String userId
    ) {
        // Prepare JSON body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("ticketId", id);
        requestBody.put("message", message);
        requestBody.put("userId", userId);

        // Send HTTP POST to external API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        Ticket ticket = ticketRepository.findTicketById(id);
        TicketMessage newMessage = new TicketMessage();
        newMessage.setTicket(ticket);
        newMessage.setMessage(message);
        newMessage.setTimestamp(LocalDateTime.now());
        newMessage.setUserId(userId);
        newMessage.setFromAssignee(true);
        ticketMessageRepository.save(newMessage);

        try {
            restTemplate.postForEntity("https://6579-14-195-129-62.ngrok-free.app/api/sendReply", requestEntity, String.class);
        } catch (Exception e) {
            // You can log or handle the error as needed
            System.err.println("Failed to send reply: " + e.getMessage());
        }
        return "redirect:/ticket/" + id;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "dashboard";
    }

    @GetMapping("/ticket/{id}")
    public String ticketDetail(@PathVariable String id, Model model) {
        Ticket ticket = ticketRepository.findTicketById(id);
        List<TicketMessage> messages = ticketMessageRepository.findByTicketIdOrderByTimestampAsc(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("messages", messages);
        return "ticket-details";
    }

    @PostMapping("/ticket/{id}/reply")
    public String replyToTicket(@PathVariable String id , @RequestBody ReplyRequest request) {
        Ticket ticket = ticketRepository.findTicketById(id);
        TicketMessage newMessage = new TicketMessage();
        newMessage.setTicket(ticket);
        newMessage.setMessage(request.getMessage());
        newMessage.setTimestamp(LocalDateTime.now());
        newMessage.setUserId(request.getUserId());
        newMessage.setFromAssignee(false);
        ticketMessageRepository.save(newMessage);
        return "redirect:/ticket/" + id;
    }
}
