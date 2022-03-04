package com.system559.diningout.repository;

import com.system559.diningout.model.Guest;
import com.system559.diningout.model.Ticket;
import com.system559.diningout.model.TicketTier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {
    Optional<Ticket> findByGuest(Guest guest);
    Optional<Ticket> findByPaymentIntent(String paymentIntent);

    void deleteByGuest(Guest guest);
}
