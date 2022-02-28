package com.system559.diningout.repository;

import com.system559.diningout.model.Guest;
import com.system559.diningout.model.Meal;
import com.system559.diningout.model.Grade;
import com.system559.diningout.model.Unit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends MongoRepository<Guest,String> {
    List<Guest> findByLastName(String lastName);
    List<Guest> findByGrade(Grade grade);
    List<Guest> findByMeal(Meal meal);
    //How do I find by a single request?
    List<Guest> findByUnit(Unit unit);
    Optional<Guest> findByPartner(Guest partner);
    Optional<Guest> findByEmail(String email);
}
