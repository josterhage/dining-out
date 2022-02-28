package com.system559.diningout.repository;

import com.system559.diningout.model.Salute;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SaluteRepository extends MongoRepository<Salute,String> {
    Optional<Salute> findByName(String name);
    Optional<Salute> findById(String id);
}
