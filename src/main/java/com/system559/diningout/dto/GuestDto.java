package com.system559.diningout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuestDto {
    private String firstName;
    private String lastName;
    private String grade;
    private String salute;
    private String meal;
    private String requestText;
    private String unit;
    private String email;
    private String partnerId;
    private boolean confirmed;
}
