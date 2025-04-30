package com.superops.courier.slack.controller;

import com.superops.courier.slack.entity.Ticket;
import com.superops.courier.slack.entity.TicketMessage;
import com.superops.courier.slack.entity.User;
import com.superops.courier.slack.model.ReplyRequest;
import com.superops.courier.slack.model.UserChannelSelection;
import com.superops.courier.slack.repository.TicketMessageRepository;
import com.superops.courier.slack.repository.TicketRepository;
import com.superops.courier.slack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private UserRepository userRepository;

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @PostMapping("/ticket/{ticketId}/replyToUser")
    public String replyToUser(@PathVariable String ticketId,
                              @ModelAttribute ReplyRequest request) {
        // Prepare JSON body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("ticketId", ticketId);
        requestBody.put("message", request.getMessage());
        requestBody.put("email", request.getEmail());

        // Send HTTP POST to external API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        Ticket ticket = ticketRepository.findTicketById(ticketId);
        TicketMessage newMessage = new TicketMessage();
        newMessage.setTicket(ticket);
        newMessage.setMessage(request.getMessage());
        newMessage.setTimestamp(LocalDateTime.now());
        newMessage.setEmail(request.getEmail());
        newMessage.setFromAssignee(true);
        ticketMessageRepository.save(newMessage);

        try {
            restTemplate.postForEntity("https://2d3f-14-195-129-62.ngrok-free.app/api/sendReply", requestEntity, String.class);
        } catch (Exception e) {
            // You can log or handle the error as needed
            System.err.println("Failed to send reply: " + e.getMessage());
        }
        return "redirect:/ticket/" + ticketId;
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
        newMessage.setEmail(request.getEmail());
        User user = userRepository.findUserByEmail(request.getEmail());
        newMessage.setFromAssignee(user.getTechnician());
        ticketMessageRepository.save(newMessage);
        return "redirect:/ticket/" + id;
    }

    @GetMapping("/connect")
    public String installPage() {
        return "connect";
    }

    @GetMapping("/select-channels")
    public String showChannelForm(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/channels";

        List<Map<String, String>> channels = restTemplate.getForObject(url, List.class);

        model.addAttribute("channels", channels);
        model.addAttribute("userSelection", new UserChannelSelection()); // DTO for form binding

        return "channel-selection";
    }

    @PostMapping("/submit-channels")
    public String submitChannels(@ModelAttribute UserChannelSelection selection, Model model) {

        String mapChannelUrl = "http://localhost:8081/mapChannel";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserChannelSelection> entity = new HttpEntity<>(selection, headers);

        try {
            restTemplate.postForEntity(mapChannelUrl, entity, String.class);
        } catch (Exception e) {
            // Log or handle error as needed
            e.printStackTrace();
        }

        return "redirect:/dashboard";
    }
}
