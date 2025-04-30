package com.superops.courier.slack.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    private String id;
    private String client;
    private String email;
    private String subject;
    private String description;
    private Status status;
    private Priority priority;
    private String category;
    private String technician;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Provider provider;
}