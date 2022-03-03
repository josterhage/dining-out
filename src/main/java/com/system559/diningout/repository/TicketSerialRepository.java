package com.system559.diningout.repository;

import com.system559.diningout.model.TicketSerial;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketSerialRepository extends MongoRepository<TicketSerial, String> {
}
