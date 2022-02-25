package com.system559.diningout.repository;

import com.system559.diningout.model.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken,String> {
    Optional<ConfirmationToken> findByToken(String token);
}
