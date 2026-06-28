package com.anor.station.entity;

import com.anor.station.domain.SlotStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "slot",
       uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "slot_number"}))
@Getter @Setter @NoArgsConstructor
public class Slot {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status = SlotStatus.EMPTY;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "power_bank_id")
    private PowerBank powerBank;
}
