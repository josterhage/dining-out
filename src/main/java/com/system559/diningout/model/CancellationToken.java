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
    private long created;

    private static long timeToLive = 900L;

    public CancellationToken(Ticket ticket) {
        this.ticket = ticket;
        token = UUID.randomUUID().toString();
        created = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - created > timeToLive;
    }
}
