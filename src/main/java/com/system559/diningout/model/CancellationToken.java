package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.UUID;

@Data
public class CancellationToken {
    @Id
    private String id;
    @Indexed(unique = true)
    private String token;
    private Ticket ticket;

    public CancellationToken(Ticket ticket) {
        this.ticket = ticket;
        token = UUID.randomUUID().toString();
    }
}
