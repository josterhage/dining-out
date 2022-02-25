package com.system559.diningout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GuestDto {
    private String firstName;
    private String lastName;
    private String grade;
    private String title;
    private String meal;
    private List<String> requests;
    private String requestText;
    private String unit;
    private String email;
    private String partnerId;
    private boolean confirmed;
}
