// kafka/RentalCommandConsumer.java
package com.anor.station.kafka;

import com.anor.station.event.*;
import com.anor.station.service.DispenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RentalCommandConsumer {

    private final DispenseService dispenseService;

    @KafkaListener(topics = "dispense-commands", groupId = "station-service")
    public void onDispense(DispenseCommand command) {
        log.info("Received dispense command for rental {}", command.rentalId());
        dispenseService.handleDispense(command);
    }

    @KafkaListener(topics = "return-commands", groupId = "station-service")
    public void onReturn(ReturnCommand command) {
        log.info("Received return command for rental {}", command.rentalId());
        dispenseService.handleReturn(command);
    }
}
