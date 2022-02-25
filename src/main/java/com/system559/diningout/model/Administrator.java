package com.system559.diningout.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class Administrator {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
    @Indexed(unique = true)
    private String email;


}
