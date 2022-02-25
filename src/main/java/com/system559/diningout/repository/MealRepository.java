package com.system559.diningout.repository;

import com.system559.diningout.model.Meal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MealRepository extends MongoRepository<Meal, String> {
    Optional<Meal> findByName(String name);
}
