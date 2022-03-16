package com.system559.diningout.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminTicketView {
    private long ticketSerial;
    private String unit;
    private String address;
    private String lastName;
    private String firstName;
    private String meal;
    private String email;
    private String requestText;
}
