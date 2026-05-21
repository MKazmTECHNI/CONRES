package com.example.roombooking.config;

import com.example.roombooking.repository.ReservationRepository;
import com.example.roombooking.service.ConflictChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * ConflictChecker uses prototype scope — a fresh instance is created every time
     * it is injected. This avoids sharing state between concurrent reservation
     * requests and ensures thread safety without synchronization.
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ConflictChecker conflictChecker(ReservationRepository reservationRepository) {
        return new ConflictChecker(reservationRepository);
    }
}
