package com.system559.diningout.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CheckoutDto {
    private String clientSecret;
    private List<TicketTierDto> ticketTiers;
    private long fee;
    private long total;
    private long created;
}
