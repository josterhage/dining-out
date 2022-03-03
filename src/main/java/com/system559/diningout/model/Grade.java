package com.system559.diningout.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Grade {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private TicketTier tier;

    public static List<String> civilianGrades = new ArrayList<>();

    static {
        civilianGrades.add("WS/WG5");
        civilianGrades.add("WS/WG6");
        civilianGrades.add("GS7");
        civilianGrades.add("GS8");
        civilianGrades.add("GS9");
        civilianGrades.add("GS11");
        civilianGrades.add("GS12");
        civilianGrades.add("GS13");
        civilianGrades.add("CIV");
    }
}
