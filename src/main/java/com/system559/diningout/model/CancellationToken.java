package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
public class CancellationToken {
    @Id
    private String id;

    private Guest guest;
    private String token;
    private Long createdDate;

    private final static Long timeToLive = 3600L;

    public CancellationToken(Guest guest) {
        this.guest = guest;
        createdDate = System.currentTimeMillis();
        token = UUID.randomUUID().toString();
    }

    public boolean isExpired() {return System.currentTimeMillis() - createdDate > timeToLive;}
}
