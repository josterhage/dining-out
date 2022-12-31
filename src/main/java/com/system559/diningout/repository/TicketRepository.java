package com.system559.diningout.repository;

import com.system559.diningout.model.Guest;
import com.system559.diningout.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    void deleteByGuest(Guest guest);
    @Query("{'guest._id':ObjectId(guestId)}")
    Optional<Ticket> findByGuestId(String guestId);
}
