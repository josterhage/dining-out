package com.system559.diningout.repository;

import com.system559.diningout.model.Unit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UnitRepository extends MongoRepository<Unit,String> {
    Optional<Unit> findByName(String name);
}
