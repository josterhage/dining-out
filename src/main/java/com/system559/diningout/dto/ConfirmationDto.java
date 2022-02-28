package com.system559.diningout.dto;

import lombok.Data;

@Data
public class ConfirmationDto {
    private String token;
    private boolean confirmed;
}
