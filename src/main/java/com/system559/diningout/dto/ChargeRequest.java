package com.system559.diningout.dto;

import lombok.Data;

@Data
public class ChargeRequest {
    private String tier;
    private int amount; //cents
    private String stripeEmail;
    private String stripeToken;
}
