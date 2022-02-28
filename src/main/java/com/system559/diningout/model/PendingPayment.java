package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data

public class PendingPayment {
    @Id
    private String id;

    private String clientSecret;
    private String token;

    public PendingPayment(String clientSecret, String token){
        this.clientSecret = clientSecret;
        this.token=token;
    }
}
