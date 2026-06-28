package com.anor.station.repository;

import com.anor.station.entity.PowerBank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PowerBankRepository extends JpaRepository<PowerBank, UUID> {
    Optional<PowerBank> findBySerial(String serial);
}
