package com.system559.diningout.dto;

import lombok.Data;

@Data
public class TicketTierDto {
    private String name;
    private String description;
    private long price; //in cents
}
