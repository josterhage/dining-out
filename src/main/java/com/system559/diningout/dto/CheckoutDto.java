package com.system559.diningout.dto;

import com.system559.diningout.model.TicketTier;
import lombok.Data;

@Data
public class CheckoutDto {
    private TicketTier tier;
    private long quantity;
    private String token;
}
