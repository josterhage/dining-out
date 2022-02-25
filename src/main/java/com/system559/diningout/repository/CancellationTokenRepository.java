package com.system559.diningout.repository;

import com.system559.diningout.model.CancellationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CancellationTokenRepository extends MongoRepository<CancellationToken, String> {
    Optional<CancellationToken> findByToken(String token);
}
