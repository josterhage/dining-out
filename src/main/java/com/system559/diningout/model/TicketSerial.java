package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TicketSerial {
    @Id
    private String id;

    private long nextTicketSerial;
    private long maxTicketSerial;
}
