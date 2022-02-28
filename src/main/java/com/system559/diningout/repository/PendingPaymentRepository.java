package com.system559.diningout.repository;

import com.system559.diningout.model.PendingPayment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PendingPaymentRepository extends MongoRepository<PendingPayment, String> {
    Optional<PendingPayment> findByClientSecret(String clientSecret);
    // seems unlikely...
    Optional<PendingPayment> findByToken(String token);
}
