package com.system559.diningout.repository;

import com.system559.diningout.model.Administrator;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdministratorRepository extends MongoRepository<Administrator, String> {
    Administrator findByUsername(String username);
    Administrator findByEmail(String email);
}
