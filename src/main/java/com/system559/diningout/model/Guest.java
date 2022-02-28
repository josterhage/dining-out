package com.system559.diningout.model;

import com.mongodb.lang.Nullable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@Data
@Builder
public class Guest {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private Grade grade;
    private Salute salute;
    private Meal meal;
    private String requestText;
    private Unit unit;
    @Indexed(unique = true)
    private String email;
    @Nullable
    private Guest partner;
    private boolean isConfirmed;
}
