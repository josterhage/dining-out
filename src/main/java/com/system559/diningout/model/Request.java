package com.system559.diningout.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
public class Request {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String description;
}
