package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
public class ConfirmationToken {
    @Id
    private String id;

    private Guest guest;
    private String token;
    private Long createdDate;

    private final static Long timeToLive = 900L;

    public ConfirmationToken(Guest guest) {
        this.guest = guest;
        createdDate = System.currentTimeMillis();
        token = UUID.randomUUID().toString();
    }

    //should this be in the associated Service?
    public boolean isExpired() {
        return System.currentTimeMillis() - createdDate > timeToLive;
    }
}
