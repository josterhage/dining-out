package com.system559.diningout.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Salute {
    @Id
    private String id;

    private String name;
}
