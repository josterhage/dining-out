package com.system559.diningout.repository;

import com.system559.diningout.model.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GradeRepository extends MongoRepository<Grade,String> {
    Optional<Grade> findByName(String name);
}
