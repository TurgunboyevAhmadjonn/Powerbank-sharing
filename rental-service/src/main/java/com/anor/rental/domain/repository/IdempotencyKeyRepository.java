// src/main/java/com/anor/rental/domain/repository/IdempotencyKeyRepository.java
package com.anor.rental.domain.repository;

import com.anor.rental.domain.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {

    Optional<IdempotencyKey> findByKey(String key);

    void deleteByCreatedAtBefore(OffsetDateTime threshold);
}
