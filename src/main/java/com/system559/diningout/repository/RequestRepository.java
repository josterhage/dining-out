package com.system559.diningout.repository;

import com.system559.diningout.model.Request;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RequestRepository extends MongoRepository<Request, String> {
    Optional<Request> findRequestByName(String name);
}
