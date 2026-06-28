// config/KafkaConfig.java
package com.anor.station.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {
    // Producer acks=all + idempotence is set in application.yml.
    // JsonSerializer config also there. This class is a placeholder for
    // any programmatic tuning (e.g. custom error handlers) you add later.
}


