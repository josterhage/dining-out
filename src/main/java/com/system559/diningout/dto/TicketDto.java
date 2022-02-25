package com.system559.diningout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketDto {
    private String guestId;
    private String guestTitle;
    private String guestLastName;
    private String tierId;
    private String chargeId;
}
