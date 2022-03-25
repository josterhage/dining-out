package com.system559.diningout.repository;

import com.system559.diningout.model.Guest;
import com.system559.diningout.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    void deleteByGuest(Guest guest);
    @Query("{'guest._id':ObjectId('?0')}")
    Optional<Ticket> findByGuestId(String guestId);

    @Query("{'guest.email':?0}")
    List<Ticket> findByGuestEmail(String emailAddress);
}
