package com.anor.station.entity;

import com.anor.station.domain.PowerBankStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "power_bank")
@Getter @Setter @NoArgsConstructor
public class PowerBank {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "serial", nullable = false, unique = true)
    private String serial;

    @Column(name = "charge_level", nullable = false)
    private int chargeLevel = 100; // 0-100

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PowerBankStatus status = PowerBankStatus.IN_SLOT;

    @Version
    private long version; // optimistic lock guards concurrent dispense
}
