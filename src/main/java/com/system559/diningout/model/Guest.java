package com.system559.diningout.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@Builder
public class Guest {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    @DBRef
    private Grade grade;
    private Salute salute;
    private Meal meal;
    private String requestText;
    private Unit unit;
    @Indexed(unique = true)
    private String email;
    private List<String> partnerIds;
    private boolean isConfirmed;
}
