package com.superops.courier.slack.model;

import com.superops.courier.slack.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private String id;
    private String client;
    private String userId;
    private String subject;
    private String description;
    private String status;
    private String technician;
    private Provider provider;
}
