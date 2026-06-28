package com.anor.rental.config;

import com.anor.rental.kafka.event.PaymentResultEvent;
import com.anor.rental.kafka.event.StationResponseEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // ── Generic factory (used by kafkaListenerContainerFactory) ──
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return buildConsumerFactory(Object.class);
    }

    // ── Typed factories (inject these where you need specific types) ──
    @Bean
    public ConsumerFactory<String, StationResponseEvent> stationConsumerFactory() {
        return buildConsumerFactory(StationResponseEvent.class);
    }

    @Bean
    public ConsumerFactory<String, PaymentResultEvent> paymentConsumerFactory() {
        return buildConsumerFactory(PaymentResultEvent.class);
    }

    // ── Default listener factory — uses generic consumerFactory ──
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory()); // ← calls bean directly, no injection needed
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        return factory;
    }

    // ── Station-specific listener factory ──
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StationResponseEvent> stationListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, StationResponseEvent>();
        factory.setConsumerFactory(stationConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        return factory;
    }

    // ── Payment-specific listener factory ──
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentResultEvent> paymentListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentResultEvent>();
        factory.setConsumerFactory(paymentConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        return factory;
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> targetType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetType);
        deserializer.addTrustedPackages("com.anor.*");
        deserializer.setUseTypeMapperForKey(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }
}

