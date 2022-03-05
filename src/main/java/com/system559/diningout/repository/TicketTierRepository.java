package com.system559.diningout.repository;

import com.system559.diningout.model.TicketTier;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketTierRepository extends MongoRepository<TicketTier,String> {
}
