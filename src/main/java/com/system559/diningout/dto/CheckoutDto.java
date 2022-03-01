package com.system559.diningout.dto;

import com.system559.diningout.model.TicketTier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutDto {
    private String clientSecret;
    private String tierName;
    private long tierPrice;
    private long quantity;
    private long fee;
    private long created;
}
