package com.system559.diningout.repository;

import com.system559.diningout.model.Guest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuestRepository extends MongoRepository<Guest,String> {
}
