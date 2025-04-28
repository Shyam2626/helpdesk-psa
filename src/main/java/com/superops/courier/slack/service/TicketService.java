//package com.superops.courier.slack.service;
//
//import com.superops.courier.slack.entity.Ticket;
//import com.superops.courier.slack.repository.TicketRepository;
//import com.superops.courier.slack.repository.WorkspaceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class TicketService {
//    private final Map<String, Ticket> ticketDatabase = new HashMap<>();
//
//    @Autowired
//    private TicketRepository ticketRepository;
//
//    @Value("${slack.ticket.channel.id}")
//    private String ticketChannelId;
//
//    @Autowired
//    private SlackService slackService;
//
//    @Autowired
//    private WorkspaceRepository workspaceRepository;
//
//    public Ticket createTicket(String userId, String channelId, String messageText, String teamId, String messageTs) {
//        Ticket ticket = new Ticket();
//        ticket.setUserId(userId);
//        ticket.setChannelId(channelId);
//        ticket.setSubject(messageText);
//        ticket.setOriginalMessageTs(messageTs);
//        ticket.setTeamId(teamId);
//        ticket.setRequesterId(userId);
//        ticket.setStatus("NEW");
//        ticket.setCreatedAt(Instant.now());
//        Ticket savedTicket = ticketRepository.save(ticket);
//        return ticket;
//    }
//
//    public void notifyAssignee(Ticket ticket, Assignee assignee) {
//        WorkspaceConnection assigneeWorkspace = workspaceRepository.findById(assignee.getTeamId())
//                .orElseThrow(() -> new RuntimeException("Assignee workspace not found"));
//        WorkspaceConnection requesterWorkspace = workspaceRepository.findById(ticket.getTeamId())
//                .orElseThrow(() -> new RuntimeException("Requester workspace not found"));
//        String assignmentMessage = String.format("🎫 *New ticket #%d assigned to you*\n" +
//                ">*From:* <@%s> (Organization: %s)\n" + ">*Description:* %s\n\n" +
//                "Reply in this thread to communicate with the requestor.",
//                ticket.getId(), ticket.getUserId(), requesterWorkspace.getTeamName(), ticket.getSubject());
//        ResponseEntity<String> response = slackService.sendMessage(assignee.getUserId(),
//                assignmentMessage, null,
//                assigneeWorkspace.getBotAccessToken());
//        String threadTs = slackService.extractThreadTs(response.getBody());
//        ticket.setAssigneeThreadTs(threadTs);
//        ticket.addRelatedThreadTs(threadTs);
//        ticketRepository.save(ticket);
//    }
//}
//
