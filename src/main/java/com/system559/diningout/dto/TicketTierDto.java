package com.system559.diningout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketTierDto {
    private String name;
    private String description;
    private long price; //in cents
}
