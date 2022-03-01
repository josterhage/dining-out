package com.system559.diningout.repository;

import com.system559.diningout.model.Checkout;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CheckoutRepository extends MongoRepository<Checkout,String> {
    Optional<Checkout> findByClientSecret(String clientSecret);
}
