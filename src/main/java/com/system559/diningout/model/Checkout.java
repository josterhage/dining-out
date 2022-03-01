package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Checkout {
    @Id
    private String id;

    private String clientSecret;
    private Guest guest;
    private long created;

    private static long timeToLive = 3600L;

    public Checkout(String clientSecret, Guest guest) {
        this.clientSecret = clientSecret;
        this.guest = guest;
        created = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timeToLive > created;
    }
}
