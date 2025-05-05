package com.superops.courier.slack.model;

import com.superops.courier.slack.entity.Category;
import com.superops.courier.slack.entity.Priority;
import com.superops.courier.slack.entity.Provider;
import com.superops.courier.slack.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private String ticketId;
    private String client;
    private String email;
    private String subject;
    private String description;
    private Status status;
    private Priority priority;
    private Category category;
    private String subCategory;
    private String technician;
    private Provider provider;
}
