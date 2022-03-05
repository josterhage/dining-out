package com.system559.diningout.repository;

import com.system559.diningout.model.Guest;
import com.system559.diningout.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    void deleteByGuest(Guest guest);
}
