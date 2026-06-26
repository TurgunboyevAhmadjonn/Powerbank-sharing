package com.anor.paymentservice.repository;

import com.anor.paymentservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
}
